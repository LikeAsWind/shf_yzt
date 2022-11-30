package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.*;
import com.atguigu.service.*;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/house")
@SuppressWarnings({"unchecked", "rawtypes"})
public class HouseController extends BaseController {

    @Reference
    private HouseService houseService;
    @Reference
    private HouseImageService houseImageService;

    @Reference
    private HouseBrokerService houseBrokerService;

    @Reference
    private DictService dictService;

    @Reference
    private HouseUserService houseUserService;

    @Reference
    private CommunityService communityService;

    private final static String LIST_ACTION = "redirect:/house";

    private final static String PAGE_INDEX = "house/index";
    private final static String PAGE_SHOW = "house/show";
    private final static String PAGE_CREATE = "house/create";
    private final static String PAGE_EDIT = "house/edit";
    private final static String PAGE_SUCCESS = "common/successPage";


    /**
     * 分页及带条件查询的方法
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping
    public String index(ModelMap model, HttpServletRequest request) {
        Map<String, Object> filters = getFilters(request);
        PageInfo<House> page = houseService.findPage(filters);

        model.addAttribute("page", page);
        model.addAttribute("filters", filters);
        // 获取所有的小区
        model.addAttribute("communityList", communityService.findAll());
        // 获取所有的户型
        model.addAttribute("houseTypeList", dictService.findListByDictCode("houseType"));
        // 获取所有的楼层
        model.addAttribute("floorList", dictService.findListByDictCode("floor"));
        // 获取所有的建筑结构
        model.addAttribute("buildStructureList", dictService.findListByDictCode("buildStructure"));
        // 获取朝向
        model.addAttribute("directionList", dictService.findListByDictCode("direction"));
        // 获取装修情况
        model.addAttribute("decorationList", dictService.findListByDictCode("decoration"));
        // 获取房屋的用途
        model.addAttribute("houseUseList", dictService.findListByDictCode("houseUse"));
        return PAGE_INDEX;
    }

    /**
     * 进入新增
     *
     * @param model
     * @return
     */
    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("communityList", communityService.findAll());
        model.addAttribute("houseTypeList", dictService.findListByDictCode("houseType"));
        model.addAttribute("floorList", dictService.findListByDictCode("floor"));
        model.addAttribute("buildStructureList", dictService.findListByDictCode("buildStructure"));
        model.addAttribute("directionList", dictService.findListByDictCode("direction"));
        model.addAttribute("decorationList", dictService.findListByDictCode("decoration"));
        model.addAttribute("houseUseList", dictService.findListByDictCode("houseUse"));
        return PAGE_CREATE;
    }

    /**
     * 保存新增
     *
     * @param house
     * @return
     */
    @PostMapping("/save")
    public String save(House house) {
        houseService.insert(house);

        return PAGE_SUCCESS;
    }

    /**
     * 编辑
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/edit/{id}")
    public String edit(ModelMap model, @PathVariable Long id) {
        House house = houseService.getById(id);
        model.addAttribute("house", house);

        model.addAttribute("communityList", communityService.findAll());
        model.addAttribute("houseTypeList", dictService.findListByDictCode("houseType"));
        model.addAttribute("floorList", dictService.findListByDictCode("floor"));
        model.addAttribute("buildStructureList", dictService.findListByDictCode("buildStructure"));
        model.addAttribute("directionList", dictService.findListByDictCode("direction"));
        model.addAttribute("decorationList", dictService.findListByDictCode("decoration"));
        model.addAttribute("houseUseList", dictService.findListByDictCode("houseUse"));
        return PAGE_EDIT;
    }

    /**
     * 保存更新
     *
     * @param house
     * @return
     */
    @PostMapping(value = "/update")
    public String update(House house) {

        houseService.update(house);

        return PAGE_SUCCESS;
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        houseService.delete(id);
        return LIST_ACTION;
    }


    /**
     * 发布和取消发布
     * @param id
     * @param status
     * @return
     */
    @GetMapping("/publish/{id}/{status}")
    public String publish(@PathVariable Long id, @PathVariable Integer status) {
        houseService.publish(id, status);
        return LIST_ACTION;
    }


    /**
     * 详情
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public String show(ModelMap model,@PathVariable Long id) {
        // 根据id查询房源
        House house = houseService.getById(id);
        // 根据CommunityId查询小区
        Community community = communityService.getById(house.getCommunityId());
        // 查询经纪人
        List<HouseBroker> houseBrokerList = houseBrokerService.findListByHouseId(id);
        // 查询房东
        List<HouseUser> houseUserList = houseUserService.findListByHouseId(id);
        // 查询房源图片
        List<HouseImage> houseImage1List = houseImageService.findList(id, 1);
        List<HouseImage> houseImage2List = houseImageService.findList(id, 2);

        // 将查出来的数据放到request域中
        model.addAttribute("house",house);
        model.addAttribute("community",community);
        model.addAttribute("houseBrokerList",houseBrokerList);
        model.addAttribute("houseUserList",houseUserList);
        model.addAttribute("houseImage1List",houseImage1List);
        model.addAttribute("houseImage2List",houseImage2List);
        return PAGE_SHOW;
    }
}