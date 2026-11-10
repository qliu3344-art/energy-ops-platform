package com.energy.ops.auth;

import com.energy.ops.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录校验拦截器：拦截 /api/**，从 Authorization 头里取令牌
 *
 * <p>执行位置在 DispatcherServlet 找到 Controller 之后、Controller 方法之前。
 * 拦哪些路径不在这里写，而是在 {@code WebConfig} 里注册时配置。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    // 需要手动往响应里写 JSON，所以注入 Spring 已配好的 ObjectMapper
    private final ObjectMapper objectMapper;

    /**
     * preHandle 返回 true 继续往下走，返回 false 直接中断请求。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 浏览器跨域预检请求直接放行，它本来就不带令牌
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String token = resolveToken(request);
        // 令牌无效或已过期：直接写 401 响应体，不再进入 Controller
        if (tokenService.getUsername(token) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    objectMapper.writeValueAsString(Result.error(401, "未登录或登录已过期")));
            return false;
        }
        // 校验通过就续期：只要一直在操作，登录不会因为满 2 小时被打断
        tokenService.touch(token);
        return true;
    }

    /**
     * 从请求头取令牌。标准写法是 "Authorization: Bearer xxx"，
     * 前端如果不带前缀也兼容。
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            return null;
        }
        return header.startsWith("Bearer ") ? header.substring(7) : header;
    }
}
