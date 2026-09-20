package com.tit.university.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会议类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class  Meeting {


    // 主键id
    private Long id;

    // 用户id
    private Long userId;

    // 会议名称
    private String meetingName;

    // 会议类别（国际学术会议，国内学术会议）
    private String category;

    // 是否做报告（0：否，1：是）
    private Integer hasReport;

    // 报告时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportTime;

    // 会议时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate meetingTime;


    // 会议地点
    private String meetingLocation;

    // 审核状态
    private String status;

    // 文件路径
    private String evidencePath;

    // 文件名
    private String evidenceName;

    // 文件大小（字节）
    private Long evidenceSize;

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



