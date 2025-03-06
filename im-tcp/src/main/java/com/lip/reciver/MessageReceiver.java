package com.lip.reciver;

import com.alibaba.fastjson.JSONObject;
import com.lip.im.model.constants.Constants;
import com.lip.proto.MessagePack;
import com.lip.reciver.process.BaseProcess;
import com.lip.reciver.process.ProcessFactory;
import com.lip.utils.MqFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: Elon
 * @title: MessageReceiver
 * @projectName: IM-System
 * @description: 消息接收器
 * @date: 2025/3/5 1:22
 */
@Slf4j
public class MessageReceiver {

    private static String brokerId;

    private static void startReceiverMessage() {
        try {
            Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MessageService2Im + brokerId);
            channel.queueDeclare(Constants.RabbitConstants.MessageService2Im + brokerId,
                    true, false, false, null);

            channel.queueBind(Constants.RabbitConstants.MessageService2Im + brokerId,
                    Constants.RabbitConstants.MessageService2Im, brokerId);

            channel.basicConsume(Constants.RabbitConstants.MessageService2Im + brokerId, false,
                    new DefaultConsumer(channel) {

                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            try {
                                String msgStr = new String(body);
                                log.info(msgStr);
                                MessagePack messagePack = JSONObject.parseObject(msgStr, MessagePack.class);
                                BaseProcess messageProcess = ProcessFactory.getMessageProcess(messagePack.getCommand());
                                messageProcess.process(messagePack);

                                channel.basicAck(envelope.getDeliveryTag(), false);
                            } catch (Exception e) {
                                e.printStackTrace();
                                channel.basicNack(envelope.getDeliveryTag(), false, false);
                            }
                        }
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        startReceiverMessage();
    }

    public static void init(String brokerId) {
        if (StringUtils.isBlank(MessageReceiver.brokerId)) {
            MessageReceiver.brokerId = brokerId;
        }
        startReceiverMessage();
    }
}
