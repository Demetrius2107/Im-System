package com.lip.im.imservice.user.model.req;

import com.lip.im.model.model.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author Elon
 */
@Data
public class SubscribeUserOnlineStatusReq extends RequestBase {

    private List<String> subUserId;

    private Long subTime;


}