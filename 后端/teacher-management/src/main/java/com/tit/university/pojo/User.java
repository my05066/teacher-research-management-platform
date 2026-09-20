package com.tit.university.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


/**
 * 登录用户类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {


    // id
    private Long id;

    // 工号
    private String employeeId;

    // 用户名
    private String username;
    // 邮箱
    private String email;

    // 密码
    private String password;

    // 角色（admin：管理员，teacher：教师）
    private String role;

    // 真实姓名
    private String realName;

    // 部门
    private String department;

    // 电话
    private String phone;

    // 创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 更新时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // jwt令牌
    private String token;
}