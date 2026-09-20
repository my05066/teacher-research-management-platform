CREATE DATABASE IF NOT EXISTS `teacher_management_schema` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `teacher_management_schema`;

-- ==================== 用户表 ====================
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID，主键' PRIMARY KEY,
    `employee_id` VARCHAR(50) NOT NULL COMMENT '工号，唯一',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一，用于登录',
    `email` VARCHAR(100) NOT NULL COMMENT '注册邮箱，唯一',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) DEFAULT 'teacher' COMMENT '角色：admin-管理员，teacher-教师',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `department` VARCHAR(100) COMMENT '所属部门',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    CONSTRAINT `uk_email` UNIQUE (`email`),
    CONSTRAINT `uk_employee_id` UNIQUE (`employee_id`),
    CONSTRAINT `uk_username` UNIQUE (`username`)
) COMMENT '用户信息表';

CREATE INDEX `idx_email` ON `users` (`email`);
CREATE INDEX `idx_employee_id` ON `users` (`employee_id`);
CREATE INDEX `idx_username` ON `users` (`username`);
CREATE INDEX `idx_role` ON `users` (`role`);

-- ==================== 科研项目表 ====================
CREATE TABLE IF NOT EXISTS `research_project` (
    `id` BIGINT AUTO_INCREMENT COMMENT '项目ID，主键' PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID（教师ID），关联用户表',
    `project_type` VARCHAR(10) NOT NULL COMMENT '项目类型：纵向/横向',
    `title` VARCHAR(200) NOT NULL COMMENT '项目名称',
    `status` VARCHAR(20) DEFAULT '待审核' NOT NULL COMMENT '审核状态：待审核/已通过/已驳回',
    `is_completed` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否已完成：0-未完成，1-已完成（仅横向项目）',
    `level` VARCHAR(20) NULL COMMENT '项目级别：国家级/省部级/地市级（仅纵向项目）',
    `start_time` DATE NULL COMMENT '下达时间（纵向项目）',
    `has_company` TINYINT(1) NULL COMMENT '是否签约企业：0-否，1-是（仅横向项目）',
    `company_name` VARCHAR(100) NULL COMMENT '签约企业名称（仅横向项目，has_company=1时必填）',
    `sign_time` DATE NULL COMMENT '签约时间（仅横向项目，has_company=1时必填）',
    `execution_period` VARCHAR(50) NULL COMMENT '执行周期（仅横向项目）',
    `expected_end_time` DATE NULL COMMENT '预计完成时间（仅横向项目，is_completed=0时必填）',
    `completion_doc_path` VARCHAR(500) NULL COMMENT '结题材料文件路径',
    `completion_doc_name` VARCHAR(200) NULL COMMENT '结题材料文件名',
    `completion_doc_size` BIGINT NULL COMMENT '结题材料文件大小（字节）',
    `audit_user_id` BIGINT UNSIGNED NULL COMMENT '审核人ID',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '删除标志：0-未删除，1-已删除',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`audit_user_id`) REFERENCES `users`(`id`)
) COMMENT '科研项目表' COLLATE = utf8mb4_unicode_ci;

CREATE INDEX `idx_user_id` ON `research_project` (`user_id`);
CREATE INDEX `idx_project_type` ON `research_project` (`project_type`);
CREATE INDEX `idx_status` ON `research_project` (`status`);
CREATE INDEX `idx_create_time` ON `research_project` (`create_time`);
CREATE INDEX `idx_delete_flag` ON `research_project` (`delete_flag`);

-- ==================== 发表论文表 ====================
CREATE TABLE IF NOT EXISTS `essay` (
    `id` BIGINT AUTO_INCREMENT COMMENT '论文ID，主键' PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID（教师ID），关联用户表',
    `title` VARCHAR(300) NOT NULL COMMENT '论文题目',
    `level` VARCHAR(50) NOT NULL COMMENT '发表论文等级：Science/Nature/SCI一区/.../中文核心',
    `author_type` VARCHAR(20) NOT NULL COMMENT '作者信息：第一作者/通讯作者',
    `status` VARCHAR(20) DEFAULT '待审核' NOT NULL COMMENT '审核状态：待审核/已通过/已驳回',
    `publish_time` DATE NULL COMMENT '发表时间',
    `journal_name` VARCHAR(200) NULL COMMENT '期刊名称',
    -- 照片上传字段（来自升级脚本）
    `certificate_path` VARCHAR(500) NULL COMMENT '证书照片路径',
    `certificate_name` VARCHAR(200) NULL COMMENT '证书照片文件名',
    `certificate_size` BIGINT NULL COMMENT '证书照片文件大小（字节）',
    `audit_user_id` BIGINT UNSIGNED NULL COMMENT '审核人ID',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '删除标志：0-未删除，1-已删除',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`audit_user_id`) REFERENCES `users`(`id`)
) COMMENT '发表论文表' COLLATE = utf8mb4_unicode_ci;

CREATE INDEX `idx_user_id` ON `essay` (`user_id`);
CREATE INDEX `idx_level` ON `essay` (`level`);
CREATE INDEX `idx_status` ON `essay` (`status`);
CREATE INDEX `idx_create_time` ON `essay` (`create_time`);
CREATE INDEX `idx_delete_flag` ON `essay` (`delete_flag`);

-- ==================== 专利成果表 ====================
CREATE TABLE IF NOT EXISTS `patent` (
    `id` BIGINT AUTO_INCREMENT COMMENT '专利ID，主键' PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID（教师ID），关联用户表',
    `name` VARCHAR(200) NOT NULL COMMENT '专利成果名称',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：专利/专著/软著',
    `approval_time` DATE NOT NULL COMMENT '获批时间',
    `status` VARCHAR(20) DEFAULT '待审核' NOT NULL COMMENT '审核状态：待审核/已通过/已驳回',
    `patent_number` VARCHAR(100) NULL COMMENT '专利号/证书号',
    -- 照片上传字段（来自升级脚本）
    `certificate_path` VARCHAR(500) NULL COMMENT '专利证书照片路径',
    `certificate_name` VARCHAR(200) NULL COMMENT '专利证书照片文件名',
    `certificate_size` BIGINT NULL COMMENT '专利证书照片文件大小（字节）',
    `audit_user_id` BIGINT UNSIGNED NULL COMMENT '审核人ID',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '删除标志：0-未删除，1-已删除',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`audit_user_id`) REFERENCES `users`(`id`)
) COMMENT '专利成果表' COLLATE = utf8mb4_unicode_ci;

CREATE INDEX `idx_user_id` ON `patent` (`user_id`);
CREATE INDEX `idx_type` ON `patent` (`type`);
CREATE INDEX `idx_status` ON `patent` (`status`);
CREATE INDEX `idx_create_time` ON `patent` (`create_time`);
CREATE INDEX `idx_delete_flag` ON `patent` (`delete_flag`);

-- ==================== 科研获奖表 ====================
CREATE TABLE IF NOT EXISTS `award` (
    `id` BIGINT AUTO_INCREMENT COMMENT '获奖ID，主键' PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID（教师ID），关联用户表',
    `award_name` VARCHAR(200) NOT NULL COMMENT '获奖名称',
    `level` VARCHAR(50) NOT NULL COMMENT '获奖级别',
    `award_time` DATE NOT NULL COMMENT '获奖时间',
    `status` VARCHAR(20) DEFAULT '待审核' NOT NULL COMMENT '审核状态：待审核/已通过/已驳回',
    `award_org` VARCHAR(200) NULL COMMENT '颁奖机构',
    `certificate_path` VARCHAR(500) NULL COMMENT '证书文件路径',
    `certificate_name` VARCHAR(200) NULL COMMENT '证书文件名',
    `certificate_size` BIGINT NULL COMMENT '证书文件大小（字节）',
    `audit_user_id` BIGINT UNSIGNED NULL COMMENT '审核人ID',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '删除标志：0-未删除，1-已删除',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`audit_user_id`) REFERENCES `users`(`id`)
) COMMENT '科研获奖表' COLLATE = utf8mb4_unicode_ci;

CREATE INDEX `idx_user_id` ON `award` (`user_id`);
CREATE INDEX `idx_level` ON `award` (`level`);
CREATE INDEX `idx_status` ON `award` (`status`);
CREATE INDEX `idx_create_time` ON `award` (`create_time`);
CREATE INDEX `idx_delete_flag` ON `award` (`delete_flag`);

-- ==================== 学术会议表 ====================
CREATE TABLE IF NOT EXISTS `meeting` (
    `id` BIGINT AUTO_INCREMENT COMMENT '会议ID，主键' PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID（教师ID），关联用户表',
    `meeting_name` VARCHAR(200) NOT NULL COMMENT '会议名称',
    `category` VARCHAR(50) NOT NULL COMMENT '会议类别：国际学术会议/国内学术会议',
    `has_report` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否做报告：0-否，1-是',
    `report_time` DATE NULL COMMENT '报告时间',
    `meeting_time` DATE NULL COMMENT '会议时间',
    `meeting_location` VARCHAR(200) NULL COMMENT '会议地点',
    `status` VARCHAR(20) DEFAULT '待审核' NOT NULL COMMENT '审核状态：待审核/已通过/已驳回',
    -- 使用 evidence_* 字段存储邀请函照片（原已有，仅更新注释）
    `evidence_path` VARCHAR(500) NULL COMMENT '报告邀请函照片路径',
    `evidence_name` VARCHAR(200) NULL COMMENT '报告邀请函照片文件名',
    `evidence_size` BIGINT NULL COMMENT '报告邀请函照片文件大小（字节）',
    `audit_user_id` BIGINT UNSIGNED NULL COMMENT '审核人ID',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '删除标志：0-未删除，1-已删除',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`audit_user_id`) REFERENCES `users`(`id`)
) COMMENT '学术会议表' COLLATE = utf8mb4_unicode_ci;

CREATE INDEX `idx_user_id` ON `meeting` (`user_id`);
CREATE INDEX `idx_category` ON `meeting` (`category`);
CREATE INDEX `idx_status` ON `meeting` (`status`);
CREATE INDEX `idx_create_time` ON `meeting` (`create_time`);
CREATE INDEX `idx_delete_flag` ON `meeting` (`delete_flag`);

-- ==================== 指导学生获奖表 ====================
CREATE TABLE IF NOT EXISTS `student_award` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'ID，主键' PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID（教师ID），关联用户表',
    `student_name` VARCHAR(50) NOT NULL COMMENT '学生负责人名字',
    `award_name` VARCHAR(200) NOT NULL COMMENT '获奖项目名称',
    `award_type` VARCHAR(100) NOT NULL COMMENT '获奖类型',
    `award_time` DATE NULL COMMENT '获奖时间',
    `status` VARCHAR(20) DEFAULT '待审核' NOT NULL COMMENT '审核状态：待审核/已通过/已驳回',
    `audit_user_id` BIGINT UNSIGNED NULL COMMENT '审核人ID',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '删除标志：0-未删除，1-已删除',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`audit_user_id`) REFERENCES `users`(`id`)
) COMMENT '指导学生获奖表' COLLATE = utf8mb4_unicode_ci;

CREATE INDEX `idx_user_id` ON `student_award` (`user_id`);
CREATE INDEX `idx_status` ON `student_award` (`status`);
CREATE INDEX `idx_create_time` ON `student_award` (`create_time`);
CREATE INDEX `idx_delete_flag` ON `student_award` (`delete_flag`);

-- ==================== 其他科研成果表 ====================
CREATE TABLE IF NOT EXISTS `other_achievement` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'ID，主键' PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID（教师ID），关联用户表',
    `title` VARCHAR(200) NOT NULL COMMENT '成果标题',
    `description` TEXT NULL COMMENT '成果描述',
    `status` VARCHAR(20) DEFAULT '待审核' NOT NULL COMMENT '审核状态：待审核/已通过/已驳回',
    `evidence_path` VARCHAR(500) NULL COMMENT '证明文件路径',
    `evidence_name` VARCHAR(200) NULL COMMENT '证明文件名',
    `evidence_size` BIGINT NULL COMMENT '证明文件大小（字节）',
    `audit_user_id` BIGINT UNSIGNED NULL COMMENT '审核人ID',
    `audit_time` DATETIME NULL COMMENT '审核时间',
    `audit_remark` VARCHAR(500) NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` TINYINT(1) DEFAULT 0 NOT NULL COMMENT '删除标志：0-未删除，1-已删除',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`audit_user_id`) REFERENCES `users`(`id`)
) COMMENT '其他科研成果表' COLLATE = utf8mb4_unicode_ci;

CREATE INDEX `idx_user_id` ON `other_achievement` (`user_id`);
CREATE INDEX `idx_status` ON `other_achievement` (`status`);
CREATE INDEX `idx_create_time` ON `other_achievement` (`create_time`);
CREATE INDEX `idx_delete_flag` ON `other_achievement` (`delete_flag`);

-- ==================== 研觉晓AI助手聊天记录表 ====================
CREATE TABLE ai_assistant_chat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '聊天记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role ENUM('user', 'assistant') NOT NULL COMMENT '角色：user-用户，assistant-AI助手',
    content TEXT NOT NULL COMMENT '消息内容',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) COMMENT='研觉晓AI助手聊天记录表';

-- ==================== 初始化账号 ====================
-- 避免重复插入，使用 INSERT IGNORE 或检查是否存在
INSERT IGNORE INTO `users` (`employee_id`, `username`, `email`, `password`, `role`, `real_name`, `department`)
VALUES ('ADMIN001', 'admin', '3244940957@qq.com', '123456', 'admin', '王金宇', '教务处'),
       ('T001', 'teacher1', '3244940955@qq.com', '123456', 'teacher', '张珂强', '大数据部门');