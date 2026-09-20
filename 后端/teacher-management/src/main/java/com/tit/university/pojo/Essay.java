package com.tit.university.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 论文类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Essay {




    // 主键id
    private Long id;

    // 用户id
    private Long userId;

    // 论文题目
    private String title;

    // 论文等级
    private String level;

    // 作者信息（第一作者，通讯作者）
    private String authorType;

    // 审核状态
    private String status;

    // 发表时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishTime;

    // 期刊名称
    private String journalName;

    // 证书路径
    private String certificatePath;

    // 证书名
    private String certificateName;

    // 证书大小（字节）
    private Long certificateSize;

    // 审核人id
    private Long auditUserId;

    // 审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    // 审核备注
    private String auditRemark;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 是否删除（0：未删除，1：已删除）
    private Integer deleteFlag;

    // 提交人姓名
    private String userName;

    // 提交人工号
    private String employeeId;

    // 提交人手机号
    private String phone;

    // 提交人部门
    private String department;


    // 提交人邮箱
    private String email;




}





