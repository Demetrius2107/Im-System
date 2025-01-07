package com.hua.im.app.server.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Shukun.Li
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO<T> {

    private int code;

    private String msg;

    private T data;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ResponseVO successResponse(Object data) {
        return new ResponseVO(200, "success", data);
    }

    public static <T> ResponseVO<T> successResponse() {
        return new ResponseVO<>(200, "success");
    }

    public static <T> ResponseVO<T> errorResponse() {
        return new ResponseVO<>(500, "系统内部异常");
    }

    public static <T> ResponseVO<T> errorResponse(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static <T> ResponseVO<T> errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO<>(enums.getCode(), enums.getError());
    }

    public boolean isOk(){
        return this.code == 200;
    }


    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public <T> ResponseVO<T> success(){
        this.code = 200;
        this.msg = "success";
        // Bug 修复：将 ResponseVO<T> 改为 ResponseVO<?>
        if (this instanceof ResponseVO<?>) {
            // Bug 修复：移除多余的 (ResponseVO<T>) this;
            return (ResponseVO<T>) this;
        } else {
            throw new ClassCastException("Cannot cast " + this.getClass().getName() + " to " + ResponseVO.class.getName());
        }
    }

    public <T> ResponseVO<T> success(T data){
        this.code = 200;
        this.msg = "success";
        // Bug 修复：将 ResponseVO<T> 改为 ResponseVO<?>
        if (this instanceof ResponseVO<?>) {
            // Bug 修复：添加类型参数 T
            return (ResponseVO<T>) this;
        } else {
            throw new ClassCastException("Cannot cast " + this.getClass().getName() + " to " + ResponseVO.class.getName());
        }
    }
    
}