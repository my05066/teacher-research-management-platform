package com.tit.university.service.impl;

import com.tit.university.mapper.*;
import com.tit.university.pojo.*;
import com.tit.university.service.AiAssistantService;
import com.tit.university.service.DeepSeekService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 研觉晓AI助手：汇总用户科研数据 + 调用DeepSeek问答
@Slf4j
@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    @Autowired
    private ProgramMapper programMapper;
    @Autowired
    private EssayMapper essayMapper;
    @Autowired
    private PatentMapper patentMapper;
    @Autowired
    private AwardMapper awardMapper;
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private StudentAwardMapper studentAwardMapper;
    @Autowired
    private OtherAchievementMapper otherAchievementMapper;
    @Autowired
    private DeepSeekService deepSeekService;

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String ask(Long userId, String question) {
        if (userId == null) {
            return "未获取到当前登录用户信息，请重新登录后再试。";
        }
        if (question == null || question.isBlank()) {
            return "请先输入要咨询的问题，例如：\"我在2025年5月5日之前有哪些成果？\"";
        }

        // 拉取用户的科研数据，最多取200条
        int maxRows = 200;
        List<Program> programs = trimList(programMapper.list(userId, null), maxRows);
        List<Essay> essays = trimList(essayMapper.list(userId, null), maxRows);
        List<Patent> patents = trimList(patentMapper.list(userId, null), maxRows);
        List<Award> awards = trimList(awardMapper.list(userId, null), maxRows);
        List<Meeting> meetings = trimList(meetingMapper.list(userId, null), maxRows);
        List<StudentAward> studentAwards = trimList(studentAwardMapper.list(userId, null), maxRows);
        List<OtherAchievement> others = trimList(otherAchievementMapper.list(userId, null), maxRows);

        String fullPrompt = assemblePrompt(question, programs, essays, patents, awards, meetings, studentAwards, others);
        log.debug("研觉晓prompt长度: {}", fullPrompt.length());
        return deepSeekService.chat(fullPrompt);
    }

    private <T> List<T> trimList(List<T> src, int max) {
        if (src == null || src.size() <= max) return src;
        return src.subList(0, max);
    }

    // 拼装给模型的prompt
    private String assemblePrompt(String question,
                               List<Program> programs,
                               List<Essay> essays,
                               List<Patent> patents,
                               List<Award> awards,
                               List<Meeting> meetings,
                               List<StudentAward> studentAwards,
                               List<OtherAchievement> others) {


        StringBuilder sb = new StringBuilder();
        sb.append("你是一个高校教师科研成果智能助手，名字叫“研觉晓”。")
          .append("你的任务是：基于给定的结构化科研成果数据，准确回答教师的问题，或对其科研成果做分析和建议。")
          .append("回答必须使用简体中文。")
          .append("当前日期为 ").append(LocalDate.now().format(FMT_DATE)).append("。\n\n");

        sb.append("【使用规则】\n")
          .append("1. 只能依据下面提供的数据进行回答，不要编造不存在的成果。\n")
          .append("2. 当问题涉及时间范围（例如“在2025年5月5日之前有哪些成果”），请严格根据成果中的日期字段进行过滤后再统计或列举。\n")
          .append("3. 如果数据不足以完全回答，请说明“根据当前数据无法完全确定”，但可以给出合理推断和建议。\n")
          .append("4. 当用户要求“总体分析”“告诉我哪里不足”等，请结合不同类型成果的数量、时间分布和方向给出结构化分析和改进建议。\n\n");

        sb.append("【教师科研成果数据（已按创建时间倒序，时间格式均为yyyy-MM-dd）】\n\n");

        sb.append("一、科研项目（Program）\n");
        if (programs == null || programs.isEmpty()) {
            sb.append("  - 无记录\n");
        } else {
            for (Program p : programs) {
                sb.append("  - 项目名称: ").append(safe(p.getTitle()))
                  .append("；类型: ").append(safe(p.getType()))
                  .append("；级别: ").append(safe(p.getLevel()))
                  .append("；开始时间: ").append(fmtDate(p.getStartTime()))
                  .append("；预计完成时间: ").append(fmtDate(p.getExpectedEndTime()))
                  .append("；状态: ").append(safe(p.getStatus()))
                  .append("；是否已完成: ").append(p.getIsCompleted() != null && p.getIsCompleted() == 1 ? "已完成" : "未完成")
                  .append("；创建时间: ").append(fmtDateTime(p.getCreateTime()))
                  .append("\n");
            }
        }
        sb.append("\n");

        sb.append("二、发表论文（Essay）\n");
        if (essays == null || essays.isEmpty()) {
            sb.append("  - 无记录\n");
        } else {
            for (Essay e : essays) {
                sb.append("  - 题目: ").append(safe(e.getTitle()))
                  .append("；等级: ").append(safe(e.getLevel()))
                  .append("；作者身份: ").append(safe(e.getAuthorType()))
                  .append("；期刊: ").append(safe(e.getJournalName()))
                  .append("；发表时间: ").append(fmtDate(e.getPublishTime()))
                  .append("；状态: ").append(safe(e.getStatus()))
                  .append("；创建时间: ").append(fmtDateTime(e.getCreateTime()))
                  .append("\n");
            }
        }
        sb.append("\n");

        sb.append("三、专利成果（Patent）\n");
        if (patents == null || patents.isEmpty()) {
            sb.append("  - 无记录\n");
        } else {
            for (Patent p : patents) {
                sb.append("  - 名称: ").append(safe(p.getName()))
                  .append("；类型: ").append(safe(p.getType()))
                  .append("；专利号/证书号: ").append(safe(p.getPatentNumber()))
                  .append("；获批时间: ").append(fmtDate(p.getApprovalTime()))
                  .append("；状态: ").append(safe(p.getStatus()))
                  .append("；创建时间: ").append(fmtDateTime(p.getCreateTime()))
                  .append("\n");
            }
        }
        sb.append("\n");

        sb.append("四、科研获奖（Award）\n");
        if (awards == null || awards.isEmpty()) {
            sb.append("  - 无记录\n");
        } else {
            for (Award a : awards) {
                sb.append("  - 获奖名称: ").append(safe(a.getAwardName()))
                  .append("；级别: ").append(safe(a.getLevel()))
                  .append("；获奖时间: ").append(fmtDate(a.getAwardTime()))
                  .append("；颁奖机构: ").append(safe(a.getAwardOrg()))
                  .append("；状态: ").append(safe(a.getStatus()))
                  .append("；创建时间: ").append(fmtDateTime(a.getCreateTime()))
                  .append("\n");
            }
        }
        sb.append("\n");

        sb.append("五、学术会议（Meeting）\n");
        if (meetings == null || meetings.isEmpty()) {
            sb.append("  - 无记录\n");
        } else {
            for (Meeting m : meetings) {
                sb.append("  - 会议名称: ").append(safe(m.getMeetingName()))
                  .append("；类别: ").append(safe(m.getCategory()))
                  .append("；是否做报告: ").append(m.getHasReport() != null && m.getHasReport() == 1 ? "是" : "否")
                  .append("；报告时间: ").append(fmtDate(m.getReportTime()))
                  .append("；会议时间: ").append(fmtDate(m.getMeetingTime()))
                  .append("；地点: ").append(safe(m.getMeetingLocation()))
                  .append("；状态: ").append(safe(m.getStatus()))
                  .append("；创建时间: ").append(fmtDateTime(m.getCreateTime()))
                  .append("\n");
            }
        }
        sb.append("\n");

        sb.append("六、指导学生获奖（StudentAward）\n");
        if (studentAwards == null || studentAwards.isEmpty()) {
            sb.append("  - 无记录\n");
        } else {
            for (StudentAward s : studentAwards) {
                sb.append("  - 学生负责人: ").append(safe(s.getStudentName()))
                  .append("；获奖项目: ").append(safe(s.getAwardName()))
                  .append("；获奖类型: ").append(safe(s.getAwardType()))
                  .append("；获奖时间: ").append(fmtDate(s.getAwardTime()))
                  .append("；状态: ").append(safe(s.getStatus()))
                  .append("；创建时间: ").append(fmtDateTime(s.getCreateTime()))
                  .append("\n");
            }
        }
        sb.append("\n");

        sb.append("七、其他科研成果（OtherAchievement）\n");
        if (others == null || others.isEmpty()) {
            sb.append("  - 无记录\n");
        } else {
            for (OtherAchievement o : others) {
                sb.append("  - 标题: ").append(safe(o.getTitle()))
                  .append("；描述: ").append(safe(o.getDescription()))
                  .append("；状态: ").append(safe(o.getStatus()))
                  .append("；创建时间: ").append(fmtDateTime(o.getCreateTime()))
                  .append("\n");
            }
        }
        sb.append("\n");

        sb.append("【用户问题】\n");
        sb.append(question).append("\n\n");

        sb.append("【回答要求】\n")
          .append("1. 先用1-2句话直接回答用户问题的核心结论，然后再分点详细说明。\n")
          .append("2. 如果问题是时间范围统计类，请给出按类型/时间的清单或统计汇总。\n")
          .append("3. 如果问题是“总体分析/哪里不足”，请从成果数量、时间分布、结构（项目/论文/专利/获奖等）、与常规要求对比等维度分析，并给出具体可执行的改进建议。\n");

        return sb.toString();
    }

    private String safe(Object val) {
        return val == null ? "无" : String.valueOf(val);
    }

    private String fmtDate(LocalDate d) {
        return d == null ? "无" : d.format(FMT_DATE);
    }

    private String fmtDateTime(java.time.LocalDateTime dt) {
        if (dt == null) return "无";
        return dt.toLocalDate().format(FMT_DATE);
    }
}

