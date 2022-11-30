package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.*;
import com.atguigu.result.Result;
import com.atguigu.service.*;
import com.atguigu.vo.HouseQueryVo;
import com.atguigu.vo.HouseVo;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/house")
@CrossOrigin
@SuppressWarnings({"unchecked", "rawtypes"})
public class HouseController {

    @Reference
    private HouseService houseService;

    @Reference
    private CommunityService communityService;

    @Reference
    private DictService dictService;

    @Reference
    private HouseImageService houseImageService;

    @Reference
    private HouseBrokerService houseBrokerService;

    @Reference
    private UserFollowService userFollowService;

    /**
     * 房源列表
     *
     * @return
     */
    @PostMapping(value = "/list/{pageNum}/{pageSize}")
    public Result findListPage(@RequestBody HouseQueryVo houseQueryVo,
                               @PathVariable Integer pageNum,
                               @PathVariable Integer pageSize) {
        PageInfo<HouseVo> pageInfo = houseService.findPageList(pageNum, pageSize, houseQueryVo);
        return Result.ok(pageInfo);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id, HttpServletRequest request) {
        //查询房源信息
        House house = houseService.getById(id);
        //查询小区信息
        Community community = communityService.getById(house.getCommunityId());
        //获取房源的经纪人
        List<HouseBroker> houseBrokerList = houseBrokerService.findListByHouseId(id);
        //获取房源的房源图片
        List<HouseImage> houseImage1List = houseImageService.findList(id, 1);

        Map<String, Object> map = new HashMap<>();
        map.put("house", house);
        map.put("community", community);
        map.put("houseBrokerList", houseBrokerList);
        map.put("houseImage1List", houseImage1List);
        //关注业务后续补充，当前默认返回未关注
        //map.put("isFollow",false);
        //设置一个变量
        boolean isFollow = false;
        //获取用户
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("user");
        if (userInfo != null) {
            //证明已经登陆，调用UserFollowService中查询是否关注该房源的方法
            isFollow = userFollowService.isFollowed(userInfo.getId(), id);

        }
        map.put("isFollow", isFollow);
        return Result.ok(map);
    }

}