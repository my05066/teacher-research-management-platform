package com.tit.university.service;

import com.tit.university.pojo.User;

public interface LoginService {



    /**
     * 用户登录
     */
    User login(User user);


    /**
     * 用户注册
     */
    void register(User user);

    /**
     * 根据ID查询用户信息
     */
    User getUserById(Long id);

    /**
     *  修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);


    /**
     * 更新用户信息
     */
    void updateUserInfo(User user);
}
