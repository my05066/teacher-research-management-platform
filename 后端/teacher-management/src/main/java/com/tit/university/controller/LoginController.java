package com.tit.university.controller;

import com.tit.university.pojo.Result;
import com.tit.university.pojo.User;
import com.tit.university.properties.JwtProperties;
import com.tit.university.service.LoginService;
import com.tit.university.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private LoginService loginService;
//    @Autowired
//    private JwtProperties jwtProperties;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("用户登录, username={}", user.getUsername());
        User u = loginService.login(user);
        return Result.success(u);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            loginService.register(user);
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return Result.error("注册失败：" + e.getMessage());
        }
        return Result.success();
    }



    /**
     *
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result getUserInfo(@PathVariable Long id) {
        User u = loginService.getUserById(id);
        // 脱敏密码
        if (u != null) {
            u.setPassword(null);
        }
        return Result.success(u);
    }


    /**
     * 修改密码
     */
    @PutMapping("/{id}/password")
    public Result updatePassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        try {
            loginService.updatePassword(id, oldPwd, newPwd);
        } catch (Exception e) {
            log.error("修改密码失败, userId={}", id);
            return Result.error(e.getMessage());
        }
        return Result.success();
    }





    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result updateUserInfo(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        try {
            loginService.updateUserInfo(user);
        } catch (Exception e) {
            log.error("更新用户信息失败, id={}", id);
            return Result.error("更新失败：" + e.getMessage());
        }
        return Result.success();
    }
}

