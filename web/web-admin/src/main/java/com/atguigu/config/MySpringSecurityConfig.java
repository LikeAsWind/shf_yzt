package com.atguigu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration      // 声明当前类是一个配置类，注意：当前配置类也需要被扫描
@EnableWebSecurity  // 开启SpringSecurity的自动配置，会生成一个登陆页面
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启controller中方法的权限控制
public class MySpringSecurityConfig extends WebSecurityConfigurerAdapter {

    // 在内存中设置一个认证的用户名密码
/*    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(new BCryptPasswordEncoder().encode("111111"))
                .roles("");
    }*/

    // 创建一个密码加密器放到ioc容器中
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //允许iframe嵌套显示
        http.headers().frameOptions().disable();
        //登录设置
        http
                .authorizeRequests()
                .antMatchers("/static/**", "/login").permitAll()  //允许匿名用户访问的路径
                .anyRequest().authenticated()    // 其它页面全部需要验证
                .and()
                .formLogin()
                .loginPage("/login")    //用户未登录时，访问任何需要权限的资源都转跳到该路径，即登录页面，此时登陆成功后会继续跳转到第一次访问的资源页面（相当于被过滤了一下）
                .defaultSuccessUrl("/") //登录认证成功后默认转跳的路径，意思时admin登录后也跳转到/user
                .and()
                .logout()
                .logoutUrl("/logout")   //退出登陆的路径，指定spring security拦截的注销url,退出功能是security提供的
                .logoutSuccessUrl("/login")//用户退出后要被重定向的url
                .and()
                .csrf().disable();//关闭跨域请求伪造功能

        // 添加自定义异常入口
        http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler());
    }
}
