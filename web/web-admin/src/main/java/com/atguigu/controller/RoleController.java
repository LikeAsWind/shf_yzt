package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.Role;
import com.atguigu.service.PermissionService;
import com.atguigu.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/role")
@SuppressWarnings({"unchecked", "rawtypes"})
public class RoleController extends BaseController {

    @Reference
    private RoleService roleService;

    @Reference
    private PermissionService permissionService;

    private final static String PAGE_INDEX = "role/index";


    /**
     * 列表
     *
     * @param model
     * @return
     */
    @PreAuthorize("hasAuthority('role.show')")
    @RequestMapping
    public String index(ModelMap model, HttpServletRequest request) {
        Map<String, Object> filters = getFilters(request);
        PageInfo<Role> page = roleService.findPage(filters);

        model.addAttribute("page", page);
        model.addAttribute("filters", filters);
        return PAGE_INDEX;
    }

    @PreAuthorize("hasAuthority('role.create')")
    @GetMapping("/create")
    public String create() {
        return "role/create";
    }

    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping("/save")
    public String save(Role role, HttpServletRequest request) {
        roleService.insert(role);
        return "common/successPage";
    }

    @PreAuthorize("hasAuthority('role.edit')")
    @GetMapping("/edit/{id}")
    public String edit(ModelMap model, @PathVariable Long id) {
        Role role = roleService.getById(id);
        model.addAttribute("role", role);
        return "role/edit";
    }

    @PreAuthorize("hasAuthority('role.edit')")
    @PostMapping(value = "/update")
    public String update(Role role) {
        roleService.update(role);
        return "common/successPage";
    }

    // 删除角色
    @PreAuthorize("hasAuthority('role.delete')") // 此时只有Delete权限的时候才能调用下面的方法
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        roleService.delete(id);
        return "redirect:/role";
    }

    // 去分配权限的页面
    @PreAuthorize("hasAuthority('role.assign')")
    @GetMapping("/assignShow/{roleId}")
    public String goAssignPermission(@PathVariable("roleId") Long roleId, ModelMap map) {
        // 将角色id放到request域中
        map.put("roleId", roleId);
        // 调用permissionService根据角色id获取权限的方法
        List<Map<String, Object>> zNodes = permissionService.findPermissionByRoleId(roleId);
        // 将zNodes放到request域中
        map.put("zNodes", zNodes);
        return "role/assignShow";
    }

    /**
     * 给角色分配权限
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    @PreAuthorize("hasAuthority('role.assign')")
    @PostMapping("/assignPermission")
    public String assignPermission(Long roleId, Long[] permissionIds) {
        // 调用permissionService中分配权限的方法
        permissionService.saveRolePermissionRealtionShip(roleId, permissionIds);
        return "common/successPage";
    }


}