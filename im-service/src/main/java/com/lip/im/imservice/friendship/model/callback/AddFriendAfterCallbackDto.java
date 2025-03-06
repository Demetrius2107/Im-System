package com.lip.im.imservice.friendship.model.callback;

import com.lip.im.imservice.friendship.model.req.FriendDto;
import lombok.Data;

@Data
public class AddFriendAfterCallbackDto {

    private String fromId;

    private FriendDto toItem;
}
