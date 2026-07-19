package com.vela.im.service.conversation.application.dto;

import com.vela.im.shared.types.RequestBase;
import lombok.Data;

@Data
public class UpdateConversationReq extends RequestBase {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private String fromId;


}
