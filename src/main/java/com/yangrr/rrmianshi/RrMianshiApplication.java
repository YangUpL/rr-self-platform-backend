package com.yangrr.rrmianshi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yangrr.rrmianshi.mapper")
public class RrMianshiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RrMianshiApplication.class, args);
    }

}
