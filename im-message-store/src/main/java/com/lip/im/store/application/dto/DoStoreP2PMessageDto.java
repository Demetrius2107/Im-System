package com.lip.im.store.application.dto;


import com.lip.im.store.domain.entity.ImMessageBodyEntity;
import com.lip.im.shared.types.message.MessageContent;
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
