package com.lip.message.model;


import com.lip.message.dao.ImMessageBodyEntity;
import com.lip.model.message.GroupChatMessageContent;
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
