package com.lip.im.store;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.lip.im.store.infrastructure.persistence.mapper",
        "com.lip.im.store.domain.entity"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}


