package com.vela.im.tcp.interfaces.fegin;

import com.vela.im.shared.base.ResponseVO;
import com.vela.im.shared.types.message.CheckSendMessageReq;
import feign.Headers;
import feign.RequestLine;

/**
 * @author wanqiu
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
