package com.vela.im.store.interfaces.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vela.im.shared.constants.Constants;
import com.vela.im.shared.trace.TraceIdContext;
import com.vela.im.store.domain.entity.ImMessageBodyEntity;
import com.vela.im.store.application.dto.DoStoreP2PMessageDto;
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
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author wanqiu
 * @title: StroeP2PMessageReceiver
 * @projectName: IM-System
 * @description: 个人消息储存接收器
 * @date: 2025/3/3 19:48
 */
@Service
public class StoreP2PMessageReceiver {

    private static Logger logger = LoggerFactory.getLogger(StoreP2PMessageReceiver.class);

    @Autowired
    StoreMessageService storeMessageService;


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = Constants.RabbitConstants.StoreP2PMessage, durable = "true"),
                    exchange = @Exchange(value = Constants.RabbitConstants.StoreP2PMessage, durable = "true")
            ), concurrency = "1"
    )
    public void onChatMessage(@Payload Message message,
                              @Header Map<String, Object> headers,
                              Channel channel) throws IOException {
        // 从 AMQP 消息头中解析 TraceId，绑定到当前线程 MDC
        TraceIdContext.setFromAmqpHeaders(headers);
        String msg = new String(message.getBody(), "utf-8");
        logger.info("CHAT MSG FROM QUEUE ::: {}", msg);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {
            JSONObject jsonObject = JSON.parseObject(msg);
            DoStoreP2PMessageDto doStoreP2PMessageDto = jsonObject.toJavaObject(DoStoreP2PMessageDto.class);
            ImMessageBodyEntity messageBody = jsonObject.getObject("messageBody", ImMessageBodyEntity.class);
            doStoreP2PMessageDto.setImMessageBodyEntity(messageBody);
            storeMessageService.doStoreP2PMessage(doStoreP2PMessageDto);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            logger.error("处理消息时出现异常:{}", e.getMessage());
            logger.error("RMQ_CHAT_TRAN_ERROR", e);
            logger.error("NACK_MSG:{}", msg);
            // 第一个false 标识不批量拒绝 第二个false标识不重回队列
            channel.basicNack(deliveryTag, false, false);
        } finally {
            // 清理 MDC，避免线程池复用导致上下文污染
            TraceIdContext.clear();
        }

    }

}
