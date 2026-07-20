package com.vela.im.shared.base;

import com.vela.im.shared.exception.ApplicationExceptionEnum;
import lombok.Data;

/**
 * <p>Title: ResponseVO</p>
 * <p>Description: 统一API响应对象，封装返回码、消息和数据，所有接口统一使用此对象返回。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class ResponseVO<T> {

    /** 返回码，0-成功，非0-失败 */
    private int code;

    /** 返回消息 */
    private String msg;

    /** 返回数据 */
    private T data;

    public ResponseVO() {
    }

    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @SuppressWarnings("unchecked")
    public static <T> ResponseVO<T> successResponse(Object data) {
        ResponseVO<T> resp = new ResponseVO<>();
        resp.code = 200;
        resp.msg = "success";
        resp.data = (T) data;
        return resp;
    }

    public static <T> ResponseVO<T> successResponse() {
        ResponseVO<T> resp = new ResponseVO<>();
        resp.code = 200;
        resp.msg = "success";
        return resp;
    }

    public static <T> ResponseVO<T> errorResponse() {
        ResponseVO<T> resp = new ResponseVO<>();
        resp.code = 500;
        resp.msg = "系统内部异常";
        return resp;
    }

    public static <T> ResponseVO<T> errorResponse(int code, String msg) {
        ResponseVO<T> resp = new ResponseVO<>();
        resp.code = code;
        resp.msg = msg;
        return resp;
    }

    public static <T> ResponseVO<T> errorResponse(ApplicationExceptionEnum enums) {
        ResponseVO<T> resp = new ResponseVO<>();
        resp.code = enums.getCode();
        resp.msg = enums.getError();
        return resp;
    }

    public boolean isOk(){
        return this.code == 200;
    }

}
