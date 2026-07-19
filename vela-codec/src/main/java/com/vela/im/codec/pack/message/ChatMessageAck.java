package com.vela.im.codec.pack.message;

import lombok.Data;

@Data
public class ChatMessageAck {

    /** 消息ID，用于客户端确认 */
    private String messageId;

    /** 消息序列号，用于排序和去重 */
    private Long messageSequence;

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageAck(String messageId,Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }

}
