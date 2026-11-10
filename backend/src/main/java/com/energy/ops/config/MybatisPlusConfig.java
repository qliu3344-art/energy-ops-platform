package com.energy.ops.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 分页插件
 *
 * <p>{@code @Configuration} 表示这是配置类；{@code @Bean} 表示把这个方法的返回值
 * 交给 Spring 容器管理，MyBatis-Plus 启动时会自动找到它并挂到执行链上。
 *
 * <p>不注册这个插件会怎样：Mapper 方法虽然收了 Page 参数，但 SQL 里不会加 LIMIT，
 * 等于把整张表查出来再在内存里切页，total 也不对。
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页拦截器：会在执行前改写 SQL（加 LIMIT），并自动发一条 COUNT 查询
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
