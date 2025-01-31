package com.hua.im.imcommon;


import com.hua.im.imcommon.exception.ApplicationExceptionEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param <T>
 * @author Shukun.Li
 */
@Data
@Builder
@NoArgsConstructor
public class ResponseVO<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> ResponseVO<T> successResponse(T data) {
        return new ResponseVO<>(200, "success", data);
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

    public boolean isOk() {
        return this.code == 200;
    }


    private ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ResponseVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseVO<T> success() {
        this.code = 200;
        this.msg = "success";
        return this;
    }

    public ResponseVO<T> success(T data) {
        this.code = 200;
        this.msg = "success";
        this.data = data;
        return this;
    }

}
