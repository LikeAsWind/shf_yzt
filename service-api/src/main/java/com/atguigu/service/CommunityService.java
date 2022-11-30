package com.atguigu.service;

import com.atguigu.entity.Community;

import java.util.List;

public interface CommunityService extends BaseService<Community> {

    // 获取全部小区列表接口
    List<Community> findAll();

}