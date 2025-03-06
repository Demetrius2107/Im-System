package com.lip.im.imservice.conversation.model;

import com.lip.im.model.model.RequestBase;
import lombok.Data;

@Data
public class UpdateConversationReq extends RequestBase {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private String fromId;


}
