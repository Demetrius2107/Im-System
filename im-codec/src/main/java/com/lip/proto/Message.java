package com.lip.proto;

import lombok.Data;

/**
 * @author: Elon
 * @title: Message
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/3 18:26
 */
@Data
public class Message {

    private MessageHeader messageHeader;

    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}