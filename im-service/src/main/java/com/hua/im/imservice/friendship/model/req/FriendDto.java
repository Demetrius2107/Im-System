package com.hua.im.imservice.friendship.model.req;

import lombok.Data;

/**
 * @author Shukun.Li
 */
@Data
public class FriendDto {

    private String toId;

    private String remark;

    private String addSource;

    private String extra;

    private  String addWording;
}
