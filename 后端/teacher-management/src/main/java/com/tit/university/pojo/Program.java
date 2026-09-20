package com.tit.university.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 科研项目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Program {


    // 主键id
    private Long id;

    // 用户id
    private Long userId;

    // 项目类型（横向，纵向）
    private String type;

    // 项目名称
    private String title;

    // 审核状态
    private String status;

    // 是否完成（0：未完成， 1：已完成）
    private Integer isCompleted;

    // 项目级别（纵向）
    private String level;

    // 下达时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    //是否签约企业（0：否，1：是）
    private Integer hasCompany;

    // 企业名称
    private String companyName;

    // 签约时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate signTime;

    // 执行周期
    private String executionPeriod;

    // 预计完成时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedEndTime;

    // 结题材料路径
    private String completionDocPath;

    // 结题材料名
    private String completionDocName;

    // 结题材料大小（字节）
    private Long completionDocSize;

    //审核人id
    private Long auditUserId;

    //审核时间
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