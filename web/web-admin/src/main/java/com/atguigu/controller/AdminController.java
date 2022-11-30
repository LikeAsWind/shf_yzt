package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.Admin;
import com.atguigu.service.AdminService;
import com.atguigu.service.RoleService;
import com.atguigu.util.QiniuUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/admin")
@SuppressWarnings({"unchecked", "rawtypes"})
public class AdminController extends BaseController {

    @Reference
    private AdminService adminService;

    @Reference
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final static String PAGE_UPLOED_SHOW = "admin/upload";
    private final static String LIST_ACTION = "redirect:/admin";

    private final static String PAGE_INDEX = "admin/index";
    private final static String PAGE_CREATE = "admin/create";
    private final static String PAGE_EDIT = "admin/edit";
    private final static String PAGE_SUCCESS = "common/successPage";

    /**
     * 列表
     *
     * @param model
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('admin.show')")
    @RequestMapping
    public String index(ModelMap model, HttpServletRequest request) {
        Map<String, Object> filters = getFilters(request);
        PageInfo<Admin> page = adminService.findPage(filters);

        model.addAttribute("page", page);
        model.addAttribute("filters", filters);
        return PAGE_INDEX;
    }

    /**
     * 进入新增页面
     *
     * @return
     */
    @PreAuthorize("hasAuthority('admin.create')")
    @GetMapping("/create")
    public String create() {
        return PAGE_CREATE;
    }

    /**
     * 保存新增
     *
     * @param admin
     * @return
     */
    @PreAuthorize("hasAuthority('admin.create')")
    @PostMapping("/save")
    public String save(Admin admin) {
        String username = admin.getUsername();
        // 对admin对象的密码进行加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        if (username == null) {
            //设置默认头像
            admin.setHeadUrl("http://47.93.148.192:8080/group1/M00/03/F0/rBHu8mHqbpSAU0jVAAAgiJmKg0o148.jpg");
            adminService.insert(admin);
        }


        return PAGE_SUCCESS;
    }

    /**
     * 进入编辑页面
     *
     * @param model
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('admin.edit')")
    @GetMapping("/edit/{id}")
    public String edit(ModelMap model, @PathVariable Long id) {
        Admin admin = adminService.getById(id);
        model.addAttribute("admin", admin);
        return PAGE_EDIT;
    }

    /**
     * 保存更新
     *
     * @param admin
     * @return
     */
    @PreAuthorize("hasAuthority('admin.edit')")
    @PostMapping(value = "/update")
    public String update(Admin admin) {

        adminService.update(admin);

        return PAGE_SUCCESS;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('admin.delete')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        adminService.delete(id);
        Admin admin = adminService.getById(id);
        QiniuUtil.deleteFileFromQiniu(admin.getHeadUrl());
        return LIST_ACTION;
    }

    //上传头像的页面
    @PreAuthorize("hasAuthority('admin.upload')")
    @GetMapping("/uploadShow/{id}")
    public String uploadShow(ModelMap model, @PathVariable Long id) {
        model.addAttribute("id", id);
        return PAGE_UPLOED_SHOW;
    }

    //上传头像
    @PreAuthorize("hasAuthority('admin.upload')")
    @PostMapping("/upload/{id}")
    public String upload(@PathVariable Long id, @RequestParam(value = "file") MultipartFile file, HttpServletRequest request) {
        try {
            String newFileName = UUID.randomUUID().toString();
            // 获取字节数组
            byte[] bytes = file.getBytes();
            // 上传图片
            QiniuUtil.upload2Qiniu(bytes, newFileName);
            String url = "http://rlf8d4z5u.hn-bkt.clouddn.com/" + newFileName;
            Admin admin = new Admin();
            admin.setId(id);
            admin.setHeadUrl(url);
            adminService.update(admin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "common/successPage";
    }

    // 去分配角色的页面
    @PreAuthorize("hasAuthority('admin.assgin')")
    @RequestMapping("/assignShow/{adminId}")
    public String goAssignShowPage(@PathVariable Long adminId, ModelMap map) {
        // 将用户id放到request域
        map.addAttribute("adminId", adminId);
        // 调用roleService中根据用户id查询用户角色方法
        Map<String, Object> roleByAdminId = roleService.findRoleByAdminId(adminId);
        // 将map放到request域中
        map.addAllAttributes(roleByAdminId);
        return "admin/assignShow";
    }
    @PreAuthorize("hasAuthority('admin.assgin')")
    // 分配角色
    @RequestMapping("/assignRole")
    public String assignShow(Long adminId, Long[] roleIds) {
        // 调用roleService中分配角色的方法
        roleService.assignRole(adminId, roleIds);
        return "common/successPage";
    }
}
