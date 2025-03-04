package com.lip.reciver.process;

import com.lip.proto.MessagePack;
import com.lip.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author: Elon
 * @title: BaseProcess
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/5 1:36
 */
public abstract class BaseProcess {

    public abstract void processBefore();

    public void process(MessagePack messagePack) {
        processBefore();
        NioSocketChannel channel = SessionSocketHolder.get(messagePack.getAppId(),
                messagePack.getToId(), messagePack.getClientType(),
                messagePack.getImei());
        if (channel != null) {
            channel.writeAndFlush(messagePack);
        }
        processAfter();
    }


    public abstract void processAfter();
}
