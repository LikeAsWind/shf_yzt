package com.atguigu.service.impl;


import com.atguigu.dao.AdminRoleDao;
import com.atguigu.dao.BaseDao;
import com.atguigu.dao.RoleDao;
import com.atguigu.entity.Role;
import com.atguigu.service.Impl.BaseServiceImpl;
import com.atguigu.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service(interfaceClass = RoleService.class)
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AdminRoleDao adminRoleDao;

    @Override
    protected BaseDao<Role> getEntityDao() {
        return roleDao;
    }

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public Map<String, Object> findRoleByAdminId(Long adminId) {
        // 获取role所有的角色
        List<Role> roleList = roleDao.findAll();
        // 获取用户已有的角色的id
        List<Long> roleIds = adminRoleDao.findRoleIdsByAdminId(adminId);
        // 未选中的角色
        List<Role> noAssginRoleList = new ArrayList<>();
        // 选中的角色
        List<Role> assginRoleList  = new ArrayList<>();;
        // 遍历所有的角色
        for (Role role : roleList){
            // 判断当前角色id在不在roleids中
            if(roleIds.contains(role.getId())){
                // 将当前角色放到已选到的集合里面
                assginRoleList.add(role);
            }else {
                // 证明当前角色是未选中的角色，放到未选中的集合中
                noAssginRoleList.add(role);
            }
        }
        Map<String, Object> roleMap = new HashMap<String, Object>();
        roleMap.put("assginRoleList",assginRoleList);
        roleMap.put("noAssginRoleList",noAssginRoleList);
        return roleMap;
    }

    // 分配角色
    @Override
    public void assignRole(Long adminId, Long[] roleIds) {
        // 先根据用户的id将已经分配的角色删除
        adminRoleDao.deleteRoleIdsByAdminId(adminId);
        // 遍历所有的角色Id
        for (Long roleId : roleIds) {
            if(roleId != null) {
                // 将用户id和角色id插入到数据库中
                adminRoleDao.addRoleIdAndAdminId(roleId, adminId);
            }

        }
    }

}