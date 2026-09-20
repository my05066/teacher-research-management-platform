package com.tit.university.exception;

import com.tit.university.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 全局异常捕获
    @ExceptionHandler
    public Result handleException(Exception ex){
        log.error("系统异常 ： ", ex);
        return Result.error("服务开小差了，请稍后重试");
    }

    // 唯一键冲突
    @ExceptionHandler
    public Result handleDuplicateKeyException(DuplicateKeyException ex){
        log.error("数据库唯一约束异常 >> ", ex);
        String msg = ex.getMessage();
        int idx = msg.indexOf("Duplicate entry");
        if (idx < 0) {
            return Result.error("数据已存在");
        }
        String detail = msg.substring(idx);
        String[] parts = detail.split(" ");
        return Result.error(parts[2] + "已存在");
    }

    // 登录失败
    @ExceptionHandler
    public Result handleLoginFailedException(LoginFailedException ex){
        log.error("登录失败 >> {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }




}
