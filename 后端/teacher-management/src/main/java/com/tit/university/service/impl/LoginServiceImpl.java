package com.tit.university.service.impl;

import com.tit.university.exception.LoginFailedException;
import com.tit.university.pojo.Result;
import com.tit.university.pojo.User;
import com.tit.university.mapper.LoginMapper;
import com.tit.university.properties.JwtProperties;
import com.tit.university.service.LoginService;
import com.tit.university.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public User login(User user) {
        // md5加密后比对
        String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(pwd);

        User dbUser = loginMapper.selectUser(user);
        if (dbUser == null) {
            throw new LoginFailedException("用户名或密码错误");
        }
        dbUser.setPassword(null);

        // 登录成功，签发jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", dbUser.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getSecretKey(), jwtProperties.getTtl(), claims);
        dbUser.setToken(jwt);
        return dbUser;
    }

    @Override
    public void register(User user) {
        // 密码md5加密后入库
        String encPwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(encPwd);
        user.setRole("teacher");
        loginMapper.insertUser(user);
    }

    @Override
    public User getUserById(Long id) {
        return loginMapper.selectById(id);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = loginMapper.selectById(id);
        String oldMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (user == null || !user.getPassword().equals(oldMd5)) {
            throw new RuntimeException("原密码错误");
        }
        String newMd5 = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        loginMapper.updatePassword(id, newMd5, LocalDateTime.now());
    }

    @Override
    public void updateUserInfo(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        loginMapper.updateUserInfo(user);
    }
}
