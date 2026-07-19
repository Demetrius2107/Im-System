package com.vela.im.service.user.application.dto.req;

import com.vela.im.shared.types.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 */
@Data
public class SubscribeUserOnlineStatusReq extends RequestBase {

    private List<String> subUserId;

    private Long subTime;


}