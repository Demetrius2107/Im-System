package com.lip.im.imservice.user.model.req;

import com.lip.im.model.model.RequestBase;
import lombok.Data;

import java.util.List;

@Data
public class PullUserOnlineStatusReq extends RequestBase {

    private List<String> userList;

}
