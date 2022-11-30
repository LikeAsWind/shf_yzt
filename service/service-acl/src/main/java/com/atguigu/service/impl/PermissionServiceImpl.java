package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.dao.BaseDao;
import com.atguigu.dao.PermissionDao;
import com.atguigu.dao.RolePermissionDao;
import com.atguigu.entity.Permission;
import com.atguigu.entity.RolePermission;
import com.atguigu.helper.PermissionHelper;
import com.atguigu.service.Impl.BaseServiceImpl;
import com.atguigu.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service(interfaceClass = PermissionService.class)
public class PermissionServiceImpl extends BaseServiceImpl<Permission> implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Override
    protected BaseDao<Permission> getEntityDao() {
        return permissionDao;
    }

    // 根据角色id获取权限的方法
    @Override
    public List<Map<String, Object>> findPermissionByRoleId(Long roleId) {
        // 获取所有的权限
        List<Permission> permissionList = permissionDao.findAll();
        // 根据角色id查询已分配的权限id
        List<Long> permissionIds = rolePermissionDao.findPermissionIdsByRoleId(roleId);
        // 创建返回的List
        List<Map<String, Object>> returnList = new ArrayList<>();
        // 遍历所有的权限呢
        for (Permission permission : permissionList) {
            Map<String, Object> map = new HashMap();
            map.put("id", permission.getId());
            map.put("pId", permission.getParentId());
            map.put("name", permission.getName());
            //判断当前权限的id在不在permissionIds中
            if (permissionIds.contains(permission.getId())) {
                //证明该权限已经被分配
                map.put("checked", true);
            }
            // 将map放到返回的list中
            returnList.add(map);
        }

        return returnList;
    }

    /**
     * 保存角色权限
     *
     * @param roleId
     * @param permissionIds
     */
    @Override
    public void saveRolePermissionRealtionShip(Long roleId, Long[] permissionIds) {
        // 先根据用户的id将已经分配的权限删除
        rolePermissionDao.deleteByRoleId(roleId);
        // 遍历所有的角色Id
        for (Long permissionId : permissionIds) {
            if (StringUtils.isEmpty(permissionId)) {
                continue;
            }
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionDao.insert(rolePermission);
        }
    }

    @Override
    public List<Permission> getMenuPermissionByAdminId(Long userId) {
        List<Permission> permissionList = null;
        // 判断是否是系统管理员
        if (userId == 1) {
            // 证明是系统管理员,获取所有的权限
            permissionList = permissionDao.findAll();
        } else {
            // 根据用户的id查询权限菜单
            permissionList = permissionDao.getMenuPermissionByAdminId(userId);
        }
        // 通过PermissionHelper工具类将list转换成树形结构
        List<Permission> treeList = PermissionHelper.bulid(permissionList);
        return treeList;
    }

    @Override
    public List<Permission> findAllMenu() {
        //全部权限列表
        List<Permission> permissionList = permissionDao.findAll();
        if (CollectionUtils.isEmpty(permissionList)) {
            return null;
        }
        //构建树形数据,总共三级
        //把权限数据构建成树形结构数据
        List<Permission> result = PermissionHelper.bulid(permissionList);
        return result;
    }

    @Override
    public List<String> getPermissionCodeByAdminId(Long id) {
        List<String> permissionCodes = null;
        if (id == 1) {
            // 证明是系统管理员
            permissionCodes = permissionDao.getAllPermissionCode();
        } else {
            // 根据用户id查询权限码
            permissionCodes = permissionDao.getPermissionCodeByAdminId(id);
        }

        return permissionCodes;
    }
}

