package com.lip.im.model.enums.command;

public enum MediaEventCommand implements Command {

    //6000 向对方拨打语音 notify ack
    CALL_VOICE(6000),

    //6001 向对方拨打视频 notify ack
    CALL_VIDEO(6001),

    //6002 同意请求 notify ack
    ACCEPT_CALL(6002),

    //6003 同步ice
//    TRANSMIT_ICE(6003),

//    //6004 发送offer
//    TRANSMIT_OFFER(6004),

//    //6005 发送ANSWER
//    TRANSMIT_ANSWER(6005),

    //6006 hangup 挂断 notify ack
    HANG_UP(6006),

    //6007  拒绝 notify ack
    REJECT_CALL(6007),

    //6008  取消呼叫 notify ack
    CANCEL_CALL(6008),


    ;

    private Integer command;

    MediaEventCommand(int command) {
        this.command = command;
    }


    @Override
    public int getCommand() {
        return command;
    }
}
