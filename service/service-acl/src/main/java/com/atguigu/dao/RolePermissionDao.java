package com.atguigu.dao;

import com.atguigu.entity.RolePermission;

import java.util.List;

public interface RolePermissionDao  extends  BaseDao<RolePermission>{
    // 根据角色id查询已分配的权限id
    List<Long> findPermissionIdsByRoleId(Long roleId);

    // 根据用户的id将已经分配的权限删除
    void deleteByRoleId(Long roleId);
}
