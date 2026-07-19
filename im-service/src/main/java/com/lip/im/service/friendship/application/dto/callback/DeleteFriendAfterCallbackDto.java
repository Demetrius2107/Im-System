package com.lip.im.service.friendship.application.dto.callback;

import lombok.Data;

@Data
public class DeleteFriendAfterCallbackDto {

    private String fromId;

    private String toId;
}
