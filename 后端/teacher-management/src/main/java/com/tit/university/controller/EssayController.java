package com.tit.university.controller;

import com.tit.university.pojo.Essay;
import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Result;
import com.tit.university.service.EssayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/essay")
public class EssayController {


    @Autowired
    private EssayService essayService;


    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        log.info("论文分页查询 page={}", currentPage);
        return Result.success(essayService.page(currentPage, pageSize, userId, status));
    }

    @PostMapping
    public Result add(@RequestBody Essay essay, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("新增论文, userId={}", userId);
        if (userId != null) essay.setUserId(userId);
        essayService.add(essay);
        return Result.success();
    }



    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) Long auditUserId, @RequestParam(required = false) String auditRemark) {
        log.info("审核论文 id={}", id);
        essayService.audit(id, status, auditUserId, auditRemark);
        return Result.success();
    }


    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除论文 id={}", id);
        essayService.delete(id);
        return Result.success();
    }
}

