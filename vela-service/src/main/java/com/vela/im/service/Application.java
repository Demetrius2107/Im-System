package com.vela.im.service;

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
@SpringBootApplication(scanBasePackages = {"com.vela.im.service", "com.vela.im.shared"})
@MapperScan({"com.vela.im.service.*.infrastructure.persistence.mapper",
        "com.vela.im.service.user.infrastructure.persistence.mapper",
        "com.vela.im.service.friendship.infrastructure.persistence.mapper",
        "com.vela.im.service.group.infrastructure.persistence.mapper",
        "com.vela.im.service.message.infrastructure.persistence.mapper",
        "com.vela.im.service.conversation.infrastructure.persistence.mapper"})
//导入用户资料，删除用户资料，修改用户资料，查询用户资料
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
