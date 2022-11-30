package com.atguigu.dao;

import com.atguigu.entity.HouseUser;

import java.util.List;

public interface HouseUserDao extends BaseDao<HouseUser> {

    // 根据房源id查询该房源的房东
    List<HouseUser> findListByHouseId(Long houseId);
}