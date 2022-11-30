package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.UserInfo;
import com.atguigu.result.Result;
import com.atguigu.result.ResultCodeEnum;
import com.atguigu.service.UserInfoService;
import com.atguigu.util.MD5;
import com.atguigu.vo.LoginVo;
import com.atguigu.vo.RegisterVo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Reference
    private UserInfoService userInfoService;

    @GetMapping("/sendCode/{phone}")
    public Result sendCode(@PathVariable("phone") String phone, HttpSession session) {
        String code = "1111";
        session.setAttribute("code", code);
        return Result.ok(code);
    }

    // 注册
    @RequestMapping("/register")
    public Result register(@RequestBody RegisterVo registerVo, HttpSession session) {
        //获取手机号,密码，昵称和验证码
        String phone = registerVo.getPhone();
        String password = registerVo.getPassword();
        String nickName = registerVo.getNickName();
        String code = registerVo.getCode();
        //验空
        if (StringUtils.isEmpty(nickName) ||
                StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(code)) {
            //返回参数错误的消息
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        //从Session域中获取验证码
        String sessioncode = (String) session.getAttribute("code");
        //判断验证码是否正常
        if (!code.equals(sessioncode)) {
            //返回验证码错误的消息
            return Result.build(null, ResultCodeEnum.CODE_ERROR);
        }
        //调用userInfoService中的方法判断该方手机号是否已经注册
        UserInfo userInfo = userInfoService.getUserInfoByPhone(phone);
        if (null != userInfo) {
            //返回手机号已注册的消息
            return Result.build(null, ResultCodeEnum.PHONE_REGISTER_ERROR);
        }
        //创建一个UserInfo对象，然后插入到数据库中
        userInfo = new UserInfo();
        userInfo.setPhone(phone);
        userInfo.setPassword(MD5.encrypt(password));
        userInfo.setNickName(nickName);
        userInfo.setStatus(1);
        //调用UserInfoService中的插入用户的方法
        userInfoService.insert(userInfo);
        return Result.ok();

    }

    // 登陆
    @RequestMapping("/login")
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        // 获取手机号密码
        String phone = loginVo.getPhone();
        String password = loginVo.getPassword();
        // 验空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        // 根据手机号查询用户信息
        UserInfo userInfo = userInfoService.getUserInfoByPhone(phone);
        if (userInfo == null) {
            //账号不正确
            return Result.build(null, ResultCodeEnum.ACCOUNT_ERROR);
        }
        // 验证密码是否正确
        if (!MD5.encrypt(password).equals(userInfo.getPassword())) {
            //密码不正确
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }
        // 判断用户是否可用
        if (userInfo.getStatus() == 0) {
            //账号已锁定
            return Result.build(null, ResultCodeEnum.ACCOUNT_LOCK_ERROR);
        }
        // 登陆成功
        // 将用户信息放在session域，在后台判断用户是否登陆的状态
        request.getSession().setAttribute("user", userInfo);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("phone", userInfo.getPhone());
        map.put("nickName", userInfo.getNickName());
        return Result.ok(map);
    }

    @RequestMapping("/logout")
    public Result logout(HttpSession session) {
        //将session域中的userInfo对象移除
        session.removeAttribute("user");
        return Result.ok();
    }
}
