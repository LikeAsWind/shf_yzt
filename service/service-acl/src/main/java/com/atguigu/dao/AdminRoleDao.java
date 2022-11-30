package com.atguigu.dao;

import com.atguigu.entity.AdminRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminRoleDao extends BaseDao<AdminRole> {


    // 获取用户已有的角色的id
    List<Long> findRoleIdsByAdminId(Long adminId);

    // 根据用户的id将已经分配的角色删除
    void deleteRoleIdsByAdminId(Long adminId);

    // 将用户id和角色id插入到数据库中
    void addRoleIdAndAdminId(@Param("roleId") Long roleId,@Param("adminId") Long adminId);
}
