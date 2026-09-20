package com.tit.university.exception;

/**
 * 登录失败
 */
public class LoginFailedException extends RuntimeException{
    public LoginFailedException(String msg){
        super(msg);
    }
}
