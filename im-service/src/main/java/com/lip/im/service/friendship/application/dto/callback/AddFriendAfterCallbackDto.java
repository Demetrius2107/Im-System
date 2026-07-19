package com.lip.im.service.friendship.application.dto.callback;

import com.lip.im.service.friendship.application.dto.req.FriendDto;
import lombok.Data;

@Data
public class AddFriendAfterCallbackDto {

    private String fromId;

    private FriendDto toItem;
}
