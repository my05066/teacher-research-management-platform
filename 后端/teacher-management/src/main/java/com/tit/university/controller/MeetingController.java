package com.tit.university.controller;

import com.tit.university.pojo.Meeting;
import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Result;
import com.tit.university.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/meeting")
public class MeetingController {




    @Autowired
    private MeetingService meetingService;

    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize,
                      @RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        log.info("会议列表查询 page={}", currentPage);
        return Result.success(meetingService.page(currentPage, pageSize, userId, status));
    }



    @PostMapping
    public Result add(@RequestBody Meeting meeting, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("新增会议, userId={}", userId);
        if (userId != null) meeting.setUserId(userId);
        meetingService.add(meeting);
        return Result.success();
    }


    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id, @RequestParam String status,
                       @RequestParam(required = false) Long auditUserId, @RequestParam(required = false) String auditRemark) {
        log.info("审核会议 id={}", id);
        meetingService.audit(id, status, auditUserId, auditRemark);
        return Result.success();
    }



    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除会议 id={}", id);
        meetingService.delete(id);
        return Result.success();
    }
}





