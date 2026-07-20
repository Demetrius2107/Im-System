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

    MESSAGE_BODY_TOO_LARGE(50011,"消息体超过大小限制"),

    MESSAGE_FROMID_EMPTY(50012,"发送方ID不能为空"),

    MESSAGE_TOID_EMPTY(50013,"接收方ID不能为空"),

    MESSAGE_BODY_EMPTY(50014,"消息体不能为空"),

    MESSAGE_TIME_INVALID(50015,"消息时间异常"),

    MESSAGE_RATE_LIMITED(50016,"消息发送频率过高，请稍后重试"),

    MESSAGE_SELF_SEND(50017,"不能给自己发送消息"),

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
