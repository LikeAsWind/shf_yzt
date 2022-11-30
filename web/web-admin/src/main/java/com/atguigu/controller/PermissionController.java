package com.atguigu.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.Permission;
import com.atguigu.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/permission")
@SuppressWarnings({"unchecked", "rawtypes"})
public class PermissionController {

    @Reference
    private PermissionService permissionService;

    // 获取菜单
    @PreAuthorize("hasAuthority('permission.show')")
    @GetMapping
    public String index(ModelMap model) {
        List<Permission> list = permissionService.findAllMenu();
        model.addAttribute("list", list);
        return "permission/index";
    }

    // 进入新增
    @PreAuthorize("hasAuthority('permission.create')")
    @GetMapping("create")
    public String create(ModelMap model, Permission permission) {
        model.addAttribute("permission", permission);
        return "permission/create";
    }

    // 保存新增
    @PreAuthorize("hasAuthority('permission.create')")
    @PostMapping("/save")
    public String save(Permission permission) {
        permissionService.insert(permission);
        return "common/successPage";
    }

    // 编辑
    @PreAuthorize("hasAuthority('permission.edit')")
    @GetMapping("/edit/{id}")
    public String edit(ModelMap model, @PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        model.addAttribute("permission", permission);
        return "permission/edit";
    }

    // 保存更新
    @PreAuthorize("hasAuthority('permission.edit')")
    @PostMapping(value = "/update")
    public String update(Permission permission) {
        permissionService.update(permission);
        return "common/successPage";
    }

    // 删除
    @PreAuthorize("hasAuthority('permission.delete')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        permissionService.delete(id);
        return "redirect:/permission";
    }
}
