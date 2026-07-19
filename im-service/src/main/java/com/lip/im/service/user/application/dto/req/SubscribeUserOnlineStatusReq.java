package com.lip.im.service.user.application.dto.req;

import com.lip.im.shared.types.RequestBase;
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