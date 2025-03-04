package com.lip.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lip.constants.Constants;
import com.lip.message.dao.ImMessageBodyEntity;
import com.lip.message.model.DoStoreGroupMessageDto;
import com.lip.message.model.DoStoreP2PMessageDto;
import com.lip.message.service.StoreMessageService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Elon
 * @title: StoreGroupMessageReceiver
 * @projectName: IM-System
 * @description: 群组消息储存接收器
 * @date: 2025/3/3 19:48
 */
@Service
public class StoreGroupMessageReceiver {


    private static Logger logger = LoggerFactory.getLogger(StoreGroupMessageReceiver.class);


    @Autowired
    StoreMessageService storeMessageService;


    public void onChatMessage(@Payload Message message,
                              @Headers Map<String,Object> headers,
                              Channel channel) throws IOException {
        String msg = new String(message.getBody(),"utf-8");
        logger.info("CHAT MSG FROM QUEUE ::: {}",msg);
        Long deliverTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try{
            JSONObject jsonObject = JSON.parseObject(msg);
            DoStoreGroupMessageDto doStoreGroupMessageDto = jsonObject.toJavaObject(DoStoreGroupMessageDto.class);
            ImMessageBodyEntity messageBody = jsonObject.getObject("messageBody", ImMessageBodyEntity.class);
            doStoreGroupMessageDto.setImMessageBodyEntity(messageBody);
            storeMessageService.doStoreGroupMessage(doStoreGroupMessageDto);
            channel.basicAck(deliverTag, false);
        } catch (IOException e) {
            logger.error("处理消息出现异常：{}", e.getMessage());
            logger.error("RMQ_CHAT_TRAN_ERROR", e);
            logger.error("NACK_MSG:{}", msg);
            //第一个false 表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliverTag, false, false);
        }
    }

}
