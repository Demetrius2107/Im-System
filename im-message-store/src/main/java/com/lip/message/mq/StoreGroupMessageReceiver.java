package com.lip.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lip.message.dao.ImMessageBodyEntity;
import com.lip.message.model.DoStoreP2PMessageDto;
import com.lip.message.service.StoreMessageService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
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
 * @description:
 * @date: 2025/3/3 19:48
 */
@Service
public class StoreGroupMessageReceiver {


    private static Logger  logger = LoggerFactory.getLogger(StoreGroupMessageReceiver.class);

    @Autowired
    StoreMessageService storeMessageService;


    public void onChatMessage(@Payload Message message,
                              @Header Map<String ,Object> headers,
                              Channel channel) throws IOException {
        String msg = new String(message.getBody(),"utf-8");
        logger.info("CHAT MSG FROM QUEUE ::: {}",msg);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try{
            JSONObject jsonObject = JSON.parseObject(msg);
            DoStoreP2PMessageDto doStoreP2PMessageDto = jsonObject.toJavaObject(DoStoreP2PMessageDto.class);
            ImMessageBodyEntity messageBody = jsonObject.getObject("messageBody", ImMessageBodyEntity.class);
            doStoreP2PMessageDto.setImMessageBodyEntity(messageBody);
            storeMessageService.doStoreP2PMessage(doStoreP2PMessageDto);\
            channel.basicAck(deliveryTag,false);
        } catch (Exception e){
            logger.error("处理消息时出现异常:{}",e.getMessage());
            logger.error("RMQ_CHAT_TRAN_ERROR",e);
            logger.error("NACK_MSG:{}",msg);
            // 第一个false 标识不批量拒绝 第二个false标识不重回队列
            channel.basicNack(deliveryTag,false,false);
        }

    }


}
