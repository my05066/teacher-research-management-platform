package com.tit.university.controller;

import com.tit.university.pojo.Award;
import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Result;
import com.tit.university.service.AwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/award")
public class AwardController {



    @Autowired
    private AwardService awardService;

    // 分页查询
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize,
                      @RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        log.info("查询获奖列表 page={}, size={}", currentPage, pageSize);
        return Result.success(awardService.page(currentPage, pageSize, userId, status));
    }



    // 添加获奖
    @PostMapping
    public Result add(@RequestBody Award award, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("新增获奖, userId={}", userId);
        if (userId != null) award.setUserId(userId);
        awardService.add(award);
        return Result.success();
    }



    // 审核
    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id, @RequestParam String status,
                       @RequestParam(required = false) Long auditUserId, @RequestParam(required = false) String auditRemark) {
        log.info("审核获奖 id={}, status={}", id, status);
        awardService.audit(id, status, auditUserId, auditRemark);
        return Result.success();
    }





    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除获奖 id={}", id);
        awardService.delete(id);
        return Result.success();
    }
}





