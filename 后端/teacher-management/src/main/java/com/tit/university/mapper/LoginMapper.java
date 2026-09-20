package com.tit.university.mapper;

import com.tit.university.pojo.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface LoginMapper {


    /**
     * 用户登录时查找用户（账号或工号对即可）
     * @param user
     * @return
     */
    @Select("select id, employee_id employeeId, username, email, password, role, real_name realName, department, phone, created_at createdAt, updated_at updatedAt" +
            " from users " +
            "where (username = #{username} or employee_id = #{username}) and password = #{password}")
    User selectUser(User user);

    /**
     * 插入用户基本数据
     *
     */
    @Insert("insert into users (employee_id, username, email, password, role) " +
            "values (#{employeeId}, #{username}, #{email}, #{password}, #{role})")
    void insertUser(User user);


    /**
     * 根据ID查询用户
     */
    @Select("select id, employee_id employeeId, username, email, password, role, real_name realName, department, phone, created_at createdAt, updated_at updatedAt " +
            "from users " +
            "where id = #{id}")
    User selectById(Long id);

    /**
     * 更新密码
     */
    @Update("update users set password = #{password}, updated_at = #{updateTime} " +
            "where id = #{id}")
    void updatePassword(Long id, String password, LocalDateTime updateTime);



    /**
     * 更新用户信息
     */
    @Update("update users " +
            "set real_name = #{realName}, department = #{department}, phone = #{phone}, updated_at = #{updatedAt} " +
            "where id = #{id}")
    void updateUserInfo(User user);
}
