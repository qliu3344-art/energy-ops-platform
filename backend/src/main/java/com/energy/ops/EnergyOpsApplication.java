package com.energy.ops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动类。
 *
 * <p>{@code @SpringBootApplication} 是三个注解的合体：
 * <ul>
 *   <li>{@code @SpringBootConfiguration}：声明这是配置类</li>
 *   <li>{@code @EnableAutoConfiguration}：开启自动配置，按引入的 starter 自动装配 Bean</li>
 *   <li>{@code @ComponentScan}：扫描当前包及子包下的 @Controller / @Service 等</li>
 * </ul>
 * 所以它必须放在所有业务包的**最外层**，否则扫不到。
 *
 * <p>{@code @MapperScan} 告诉 MyBatis：这个包下的接口是 Mapper，
 * 启动时用动态代理生成实现类，SQL 从 resources/mapper 下同名的 XML 里找。
 */
@SpringBootApplication
@MapperScan("com.energy.ops.mapper")
public class EnergyOpsApplication {

    public static void main(String[] args) {
        // 启动内嵌 Tomcat，完成组件扫描和 Bean 装配
        SpringApplication.run(EnergyOpsApplication.class, args);
    }
}
