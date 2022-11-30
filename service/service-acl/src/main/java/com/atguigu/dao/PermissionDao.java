package com.atguigu.dao;

import com.atguigu.entity.Permission;

import java.util.List;

public interface PermissionDao extends BaseDao<Permission> {

    List<Permission> findAll();

    List<Permission> getMenuPermissionByAdminId(Long userId);

    List<String> getAllPermissionCode();

    List<String> getPermissionCodeByAdminId(Long id);
}