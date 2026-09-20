package com.tit.university.controller;

import com.tit.university.pojo.OtherAchievement;
import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Result;
import com.tit.university.service.OtherAchievementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/other-achievement")
public class OtherAchievementController {



    @Autowired
    private OtherAchievementService otherAchievementService;



    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize,
                      @RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        log.info("其他成果查询 page={}", currentPage);
        return Result.success(otherAchievementService.page(currentPage, pageSize, userId, status));
    }


    @PostMapping
    public Result add(@RequestBody OtherAchievement otherAchievement, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("新增其他成果, userId={}", userId);
        if (userId != null) otherAchievement.setUserId(userId);
        otherAchievementService.add(otherAchievement);
        return Result.success();
    }

    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id, @RequestParam String status,
                       @RequestParam(required = false) Long auditUserId, @RequestParam(required = false) String auditRemark) {
        log.info("审核其他成果 id={}", id);
        otherAchievementService.audit(id, status, auditUserId, auditRemark);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除其他成果 id={}", id);
        otherAchievementService.delete(id);
        return Result.success();
    }
}

