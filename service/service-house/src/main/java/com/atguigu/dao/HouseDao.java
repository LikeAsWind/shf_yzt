package com.atguigu.dao;

import com.atguigu.entity.House;
import com.atguigu.vo.HouseQueryVo;
import com.atguigu.vo.HouseVo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

public interface HouseDao extends BaseDao<House> {


    //前端分页及带条件查询的方法
    Page<HouseVo> findPageList(@Param("vo") HouseQueryVo houseQueryVo);
}