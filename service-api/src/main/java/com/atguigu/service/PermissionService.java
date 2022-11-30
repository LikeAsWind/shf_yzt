package com.atguigu.service;

import com.atguigu.entity.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService extends BaseService<Permission> {


    // 根据角色id获取权限的方法
    List<Map<String, Object>> findPermissionByRoleId(Long roleId);

    /**
     * 保存角色权限
     * @param roleId
     * @param permissionIds
     */
    void saveRolePermissionRealtionShip(Long roleId, Long[] permissionIds);

    List<Permission> getMenuPermissionByAdminId(Long userId);

    /**
     * 菜单全部数据
     * @return
     */
    List<Permission> findAllMenu();

    List<String> getPermissionCodeByAdminId(Long id);
}
