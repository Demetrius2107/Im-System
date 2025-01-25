package proto;

import lombok.Data;

/**
 * @author: Elon
 * @title: Message
 * @projectName: im-system
 * @description: 消息体
 * @date: 2025/1/24 21:47
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
