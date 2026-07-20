package com.vela.im.shared.base;

import com.vela.im.shared.exception.ApplicationExceptionEnum;
import lombok.Data;

/**
 * <p>Title: Result</p>
 * <p>Description: 统一API响应对象，替代 ResponseVO。封装返回码、消息和数据，所有接口统一使用此对象返回。</p>
 * <p>项目名称: Vela</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-20
 * @updateTime 2026-07-20
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
@Data
public class Result<T> {

    /** 返回码，200-成功，非200-失败 */
    private int code;

    /** 返回消息 */
    private String msg;

    /** 返回数据 */
    private T data;

    public Result() {
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // ==================== 成功工厂方法 ====================

    /**
     * 成功返回（带数据）
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return Result 实例
     */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }

    /**
     * 成功返回（无数据）
     *
     * @param <T> 数据类型
     * @return Result 实例
     */
    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        return r;
    }

    // ==================== 失败工厂方法 ====================

    /**
     * 失败返回（默认系统错误）
     *
     * @param <T> 数据类型
     * @return Result 实例
     */
    public static <T> Result<T> fail() {
        Result<T> r = new Result<>();
        r.code = 500;
        r.msg = "系统内部异常";
        return r;
    }

    /**
     * 失败返回（自定义错误码和消息）
     *
     * @param code 错误码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return Result 实例
     */
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    /**
     * 失败返回（通过错误枚举）
     *
     * @param enums 错误枚举
     * @param <T>   数据类型
     * @return Result 实例
     */
    public static <T> Result<T> fail(ApplicationExceptionEnum enums) {
        Result<T> r = new Result<>();
        r.code = enums.getCode();
        r.msg = enums.getError();
        return r;
    }

    // ==================== 便捷方法 ====================

    /**
     * 判断是否成功
     *
     * @return true 成功，false 失败
     */
    public boolean isOk() {
        return this.code == 200;
    }

}