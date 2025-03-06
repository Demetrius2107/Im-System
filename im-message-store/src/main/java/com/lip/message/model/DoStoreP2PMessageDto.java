package com.lip.message.model;


import com.lip.message.dao.ImMessageBodyEntity;
import com.lip.im.model.model.message.MessageContent;
import lombok.Data;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class DoStoreP2PMessageDto {

    private MessageContent messageContent;

    private ImMessageBodyEntity imMessageBodyEntity;

}
