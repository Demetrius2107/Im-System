package com.hua.im.imservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Shukun.Li
 */
@SpringBootApplication
@MapperScan("com.hua.im.imservice.*.dao.mapper")
public class ImServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImServiceApplication.class, args);
    }

}
