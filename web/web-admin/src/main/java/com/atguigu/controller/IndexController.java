package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.Admin;
import com.atguigu.entity.Permission;
import com.atguigu.service.AdminService;
import com.atguigu.service.PermissionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Reference
    private AdminService adminsService;

    @Reference
    private PermissionService permissionsService;

    /*//去首页
    @RequestMapping(value = "/")
    public String index() {

        return "frame/index";
    }*/

    //去首页
    @RequestMapping(value = "/")
    public String index(Map map) {
        // 设置默认的用户id
        // Long userId = 1L;
        // SecurityContextHolder
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 调用adminservice中根据用户名获取admin对象的方法
        Admin admin = adminsService.getAdminByUserName(user.getUsername());
        //根据用户的id调用PermissionService中获取用户权限菜单的方法
        List<Permission> permissionList = permissionsService.getMenuPermissionByAdminId(admin.getId());
        //将用户和用户的权限菜单放到request域中
        map.put("admin", admin);
        map.put("permissionList", permissionList);
        return "frame/index";
    }


    //去主页面
    @RequestMapping(value = "/main")
    public String main() {
        return "frame/main";
    }


    //去主页面
    @RequestMapping(value = "/login")
    public String login() {
        return "frame/login";
    }


    @GetMapping("/auth")
    public String auth() {
        return "frame/auth";
    }
}
