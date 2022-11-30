package com.atguigu.service;

import com.atguigu.entity.UserFollow;
import com.atguigu.vo.UserFollowVo;
import com.github.pagehelper.PageInfo;

public interface UserFollowService extends BaseService<UserFollow> {


    /**
     * 关注房源
     *
     * @param userId
     * @param houseId
     */
    void follow(Long userId, Long houseId);


    // 查询是否关注该房源的方法
    Boolean isFollowed(Long userId, Long houseId);

    // 分页查询我关注的房源
    PageInfo<UserFollowVo> findListPage(int pageNum, int pageSize, Long userId);

    // 取消关注房源
    void cancelFollowed(Long id);
}
