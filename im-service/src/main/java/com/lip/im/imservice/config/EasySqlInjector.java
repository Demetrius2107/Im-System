package com.lip.im.imservice.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * @author: Elon
 * @title: EasySqlInjector
 * @projectName: IM-System
 * @description: SQL注入
 * @date: 2025/3/6 19:14
 */
public class EasySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        // 添加InsertBatchSomeColumn方法
        methodList.add(new InsertBatchSomeColumn());
        return methodList;
    }
}
