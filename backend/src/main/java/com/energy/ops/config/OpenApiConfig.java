package com.energy.ops.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger（SpringDoc）文档配置
 * 访问地址：http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI energyOpsOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("能源场站设备运维管理平台 API")
                .version("1.0.0")
                .description("设备台账、巡检计划、工单与告警管理接口"));
    }
}
