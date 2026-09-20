package com.tit.university.controller;

import com.tit.university.pojo.PageResult;
import com.tit.university.pojo.Program;
import com.tit.university.pojo.ProgramQueryParam;
import com.tit.university.pojo.Result;
import com.tit.university.service.ProgramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/program")
public class ProgramController {


    @Autowired
    private ProgramService programService;

    @GetMapping
    public Result page(ProgramQueryParam queryParam, @RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
        log.info("项目分页查询 page={}, userId={}", queryParam.getCurrentPage(), userId);
        return Result.success(programService.page(queryParam, userId, status));
    }

    @PostMapping
    public Result add(@RequestBody Program program, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("新增项目, userId={}", userId);
        if (userId != null) program.setUserId(userId);
        programService.add(program);
        return Result.success();
    }

    @PutMapping("/{id}/audit")
    public Result audit(@PathVariable Long id,
                       @RequestParam String status,
                       @RequestParam(required = false) Long auditUserId,
                       @RequestParam(required = false) String auditRemark) {
        log.info("审核项目 id={}, status={}", id, status);
        programService.audit(id, status, auditUserId, auditRemark);
        return Result.success();
    }

    // 提交结题材料
    @PutMapping("/{id}/completion")
    public Result submitCompletionDoc(@PathVariable Long id,
                                     @RequestParam String completionDocPath,
                                     @RequestParam String completionDocName,
                                     @RequestParam Long completionDocSize) {
        log.info("结题材料提交 id={}", id);
        programService.submitCompletionDoc(id, completionDocPath, completionDocName, completionDocSize);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        log.info("删除项目 id={}", id);
        programService.delete(id);
        return Result.success();
    }
}
