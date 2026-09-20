package com.tit.university.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 专利
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patent {


    // 主键id
    private Long id;

    // 用户id
    private Long userId;

    // 专利名称
    private String name;

    // 专利类型（专利，专著，软著）
    private String type;

    // 获批时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvalTime;

    // 审核状态
    private String status;

    // 专利号或证书号
    private String patentNumber;

    // 专利证书路径
    private String certificatePath;


    //专利证书名
    private String certificateName;

    // 专利证书文件大小（字节）
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

    // 是否删除（0：未删除， 1：已删除）
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

