package com.lip.im.service.group.application.dto.req;


import com.lip.im.shared.types.RequestBase;
import lombok.Data;

/**
 * @author wanqiu
 * @description:
 **/
@Data
public class GetGroupReq extends RequestBase {

    private String groupId;

}
