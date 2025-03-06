package com.lip.im.imservice.conversation.model;

import com.lip.im.model.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeleteConversationReq extends RequestBase {

    @NotBlank(message = "会话id不能为空")
    private String conversationId;

    @NotBlank(message = "fromId不能为空")
    private String fromId;

}
