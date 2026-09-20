package com.tit.university.interceptor;


import com.tit.university.context.BaseContext;
import com.tit.university.properties.JwtProperties;
import com.tit.university.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

// JWT令牌校验拦截器
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getTokenName());
        String headerUserId = request.getHeader("X-User-Id");

        try {
            log.info("jwt校验: {}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            Long uid = Long.valueOf(claims.get("user_id").toString());

            if (!uid.equals(Long.valueOf(headerUserId))) {
                response.setStatus(401);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().close();
                return false;
            }

            log.info("当前用户id={}", uid);
            BaseContext.setCurrentId(uid);
            return true;
        } catch (Exception ex) {


            response.setStatus(401);
            return false;
        }
    }
}
