package com.tit.university.pojo;

import lombok.Data;

/**
 * 后端统一返回结果
 *
 */
@Data
public class Result<T> {
    private Integer code; // 编码：1为成功，0为失败
    private String msg; // 错误信息
    private T data; // 数据


    public static Result success() {
        Result r = new Result();
        r.code = 1;
        r.msg = "ok";
        return r;
    }

    public static<T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 1;
        r.msg = "ok";
        r.data = data;
        return r;
    }

    public static<T> Result<T> error(String errMsg) {
        Result r = new Result();
        r.code = 0;
        r.msg = errMsg;
        return r;
    }
}
