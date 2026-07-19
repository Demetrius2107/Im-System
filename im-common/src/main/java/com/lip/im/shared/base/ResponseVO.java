package com.lip.im.shared.base;

import com.lip.im.shared.exception.ApplicationExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO<T> {

    /** 返回码，0-成功，非0-失败 */
    private int code;

    /** 返回消息 */
    private String msg;

    /** 返回数据 */
    private T data;

    public static ResponseVO successResponse(Object data) {
        return new ResponseVO(200, "success", data);
    }

    public static ResponseVO successResponse() {
        return new ResponseVO(200, "success");
    }

    public static ResponseVO errorResponse() {
        return new ResponseVO(500, "系统内部异常");
    }

    public static ResponseVO errorResponse(int code, String msg) {
        return new ResponseVO(code, msg);
    }

    public static ResponseVO errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO(enums.getCode(), enums.getError());
    }

    public boolean isOk(){
        return this.code == 200;
    }


    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    //	this.data = null;
    }

    public ResponseVO success(){
        this.code = 200;
        this.msg = "success";
        return this;
    }

    public ResponseVO success(T data){
        this.code = 200;
        this.msg = "success";
        this.data = data;
        return this;
    }

}
