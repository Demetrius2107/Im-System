package com.vela.im.store.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vela.im.shared.constants.Constants;
import com.vela.im.store.domain.entity.ImMessageBodyEntity;
import com.vela.im.store.application.dto.DoStoreGroupMessageDto;
import com.vela.im.store.application.service.StoreMessageService;
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
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author wanqiu
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


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = Constants.RabbitConstants.StoreGroupMessage, durable = "true"),
                    exchange = @Exchange(value = Constants.RabbitConstants.StoreGroupMessage, durable = "true")
            ), concurrency = "1"
    )
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
