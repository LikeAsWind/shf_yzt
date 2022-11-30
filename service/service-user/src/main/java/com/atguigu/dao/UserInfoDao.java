package com.atguigu.dao;

import com.atguigu.entity.UserInfo;
import com.atguigu.vo.UserFollowVo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

public interface UserInfoDao extends BaseDao<UserInfo>{


    UserInfo getUserInfoByPhone(String phone);

    Page<UserFollowVo> findListPage(@Param("userId")Long userId);
}