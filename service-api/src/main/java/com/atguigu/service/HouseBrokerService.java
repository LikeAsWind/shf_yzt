package com.atguigu.service;

import com.atguigu.entity.HouseBroker;

import java.util.List;

public interface HouseBrokerService extends BaseService<HouseBroker> {

    // 根据房源id查询该房源的经纪人
    List<HouseBroker> findListByHouseId(Long houseId);
}