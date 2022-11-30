package com.atguigu.config;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.Admin;
import com.atguigu.service.AdminService;
import com.atguigu.service.PermissionService;
import com.github.pagehelper.StringUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class MyUserDetailService implements UserDetailsService {

    @Reference
    private AdminService adminService;

    @Reference
    private PermissionService permissionService;

    // 登录时，SpringSecurity会自动调用该方法，并将用户名传入该方法中
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //调用adminService根据用户名查询admin对象的方法
        Admin admin = adminService.getAdminByUserName(username);
        if (username == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        //调用permissionService中获取当前用户权限的方法
        List<String> permissionCodes = permissionService.getPermissionCodeByAdminId(admin.getId());
        //创建一个用于授权的集合
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //遍历得到每一个权限码
        for (String permissionCode : permissionCodes) {
            if (!StringUtil.isEmpty(permissionCode)) {
                // 创建GrantedAuthority对象
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(permissionCode);
                // 将对象放到集合里面
                grantedAuthorities.add(simpleGrantedAuthority);
            }
        }

        /*return new User(username, admin.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));*/
        /*
         给用户授权
            权限有两种标识方法：
            1.通过角色的方式标识，例如：ROLE_ADMIN
            2.直接设置权限 例如：Delet，Select
         */
        return new User(username, admin.getPassword(), grantedAuthorities);
    }


}
