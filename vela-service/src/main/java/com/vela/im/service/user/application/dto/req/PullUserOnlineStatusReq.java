package com.vela.im.service.user.application.dto.req;

import com.vela.im.shared.types.RequestBase;
import lombok.Data;

import java.util.List;

@Data
public class PullUserOnlineStatusReq extends RequestBase {

    private List<String> userList;

}
