package com.tit.university.controller;

import com.tit.university.pojo.Patent;
import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Result;
import com.tit.university.service.PatentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/patent")
public class PatentController {



    @Autowired
    private PatentService patentService;

    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        log.info("专利分页查询 page={}, userId={}", currentPage, userId);
        return Result.success(patentService.page(currentPage, pageSize, userId, status));
    }



    @PostMapping
    public Result add(@RequestBody Patent patent, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("新增专利, userId={}", userId);
        if (userId != null) patent.setUserId(userId);
        patentService.add(patent);
        return Result.success();
    }


    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id, @RequestParam String status,
                       @RequestParam(required = false) Long auditUserId, @RequestParam(required = false) String auditRemark) {
        log.info("审核专利 id={}", id);
        patentService.audit(id, status, auditUserId, auditRemark);
        return Result.success();
    }




    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除专利 id={}", id);
        patentService.delete(id);
        return Result.success();
    }
}




