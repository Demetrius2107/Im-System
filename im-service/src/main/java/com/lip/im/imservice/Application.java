package com.lip.im.imservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Elon
 * @title: Application
 * @projectName: IM-System
 * @description: TODO
 * @date: 2025/3/6 17:13
 */
@SpringBootApplication(scanBasePackages = {"com.lip.im.imservice", "com.lip.im.model"})
@MapperScan("com.lip.im.imservice.*.dao.mapper")
//导入用户资料，删除用户资料，修改用户资料，查询用户资料
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
