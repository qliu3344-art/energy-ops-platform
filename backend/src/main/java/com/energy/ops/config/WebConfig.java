package com.energy.ops.config;

import com.energy.ops.auth.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册登录拦截器 + 跨域配置
 *
 * <p>实现 {@link WebMvcConfigurer} 相当于在 Spring MVC 的扩展点上「挂接」自己的逻辑：
 * 框架启动时会回调这两个方法，把配置装进 MVC。
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    /**
     * 注册拦截器。注意：拦截器类上虽然有 @Component，但那只是把它创建出来，
     * 必须在这里注册并指定路径，它才会真正生效。
     *
     * <p>放行 /api/auth/login 是必须的——否则没登录就拿不到令牌，永远登录不进去。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login");
    }

    /**
     * 跨域配置。浏览器同源策略规定：5173 端口的页面不能直接请求 8080 端口，
     * 除非后端在响应头里明确允许。这里就是告诉浏览器「谁可以跨过来」。
     *
     * <p>必须允许 OPTIONS：浏览器发正式请求前会先发一个预检请求。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);   // 预检结果缓存 1 小时，减少预检次数
    }
}
