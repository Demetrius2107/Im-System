package com.lip.im.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wanqiu
 * @title: Application
 * @projectName: IM-System
 * @description: TODO
 * @date: 2025/3/6 17:13
 */
@SpringBootApplication(scanBasePackages = {"com.lip.im.service", "com.lip.im.shared"})
@MapperScan({"com.lip.im.service.*.infrastructure.persistence.mapper",
        "com.lip.im.service.user.infrastructure.persistence.mapper",
        "com.lip.im.service.friendship.infrastructure.persistence.mapper",
        "com.lip.im.service.group.infrastructure.persistence.mapper",
        "com.lip.im.service.message.infrastructure.persistence.mapper",
        "com.lip.im.service.conversation.infrastructure.persistence.mapper"})
//导入用户资料，删除用户资料，修改用户资料，查询用户资料
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
