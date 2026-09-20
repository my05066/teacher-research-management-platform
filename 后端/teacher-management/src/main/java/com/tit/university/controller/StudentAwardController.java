package com.tit.university.controller;

import com.tit.university.pojo.StudentAward;
import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Result;
import com.tit.university.service.StudentAwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/student-award")
public class StudentAwardController {


    @Autowired
    private StudentAwardService studentAwardService;



    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize,
                      @RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        log.info("学生获奖查询 page={}", currentPage);
        return Result.success(studentAwardService.page(currentPage, pageSize, userId, status));
    }



    @PostMapping
    public Result add(@RequestBody StudentAward studentAward, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("新增学生获奖, userId={}", userId);
        if (userId != null) studentAward.setUserId(userId);
        studentAwardService.add(studentAward);
        return Result.success();
    }



    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id, @RequestParam String status,
                       @RequestParam(required = false) Long auditUserId, @RequestParam(required = false) String auditRemark) {
        log.info("审核学生获奖 id={}", id);
        studentAwardService.audit(id, status, auditUserId, auditRemark);
        return Result.success();
    }



    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除学生获奖 id={}", id);
        studentAwardService.delete(id);
        return Result.success();
    }
}

