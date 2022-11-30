package com.atguigu.service;

import com.atguigu.entity.HouseImage;

import java.util.List;

public interface HouseImageService extends BaseService<HouseImage> {

    List<HouseImage> findList(Long houseId, Integer type);
}