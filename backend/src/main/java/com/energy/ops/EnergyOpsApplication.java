package com.energy.ops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.energy.ops.mapper")
public class EnergyOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyOpsApplication.class, args);
    }
}
