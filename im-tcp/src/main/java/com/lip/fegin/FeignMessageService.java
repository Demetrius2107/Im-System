package com.lip.fegin;

import com.lip.im.model.ResponseVO;
import com.lip.im.model.model.message.CheckSendMessageReq;
import feign.Headers;
import feign.RequestLine;

/**
 * @author: Elon
 * @title: FeignMessageService
 * @projectName: IM-System
 * @description:
 * @date: 2025/3/5 0:17
 */
public interface FeignMessageService {

    @Headers({"Content-Type: application/json", "Accept:application/json"})
    @RequestLine("POST /message/checkSend")
    public ResponseVO checkSendMessage(CheckSendMessageReq o);
}
