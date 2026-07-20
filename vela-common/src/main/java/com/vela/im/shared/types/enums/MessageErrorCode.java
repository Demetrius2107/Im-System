package com.vela.im.shared.types.enums;


import com.vela.im.shared.exception.ApplicationExceptionEnum;

public enum MessageErrorCode implements ApplicationExceptionEnum {


    FROMER_IS_MUTE(50002,"发送方被禁言"),

    FROMER_IS_FORBIBBEN(50003,"发送方被禁用"),


    MESSAGEBODY_IS_NOT_EXIST(50003,"消息体不存在"),

    MESSAGE_RECALL_TIME_OUT(50004,"消息已超过可撤回时间"),

    MESSAGE_IS_RECALLED(50005,"消息已被撤回"),

    MESSAGE_SEND_FAILED(50006,"消息发送失败，请重试"),

    MESSAGE_ACK_FAILED(50007,"消息确认失败"),

    MESSAGE_STORE_FAILED(50008,"消息存储失败"),

    MESSAGE_CONCURRENT_OPERATION(50009,"消息正在被操作，请稍后重试"),

    MESSAGE_CLOCK_SKEW_EXCEEDED(50010,"客户端时间偏差过大"),

    ;

    private int code;
    private String error;

    MessageErrorCode(int code, String error){
        this.code = code;
        this.error = error;
    }
    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }

}
