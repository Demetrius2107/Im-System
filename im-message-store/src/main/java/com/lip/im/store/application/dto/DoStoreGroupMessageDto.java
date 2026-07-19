package com.lip.im.store.application.dto;


import com.lip.im.store.domain.entity.ImMessageBodyEntity;
import com.lip.im.shared.types.message.GroupChatMessageContent;
import lombok.Data;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class DoStoreGroupMessageDto {

    private GroupChatMessageContent groupChatMessageContent;

    private ImMessageBodyEntity imMessageBodyEntity;

}
