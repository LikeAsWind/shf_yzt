package com.atguigu.dao;

import com.atguigu.entity.Role;
import java.util.List;


public interface RoleDao extends BaseDao<Role> {

    List<Role> findAll();
}