package com.lip.im.service.group.application.dto.req;


import com.lip.im.shared.types.RequestBase;
import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 * @description:
 **/
@Data
public class GetRoleInGroupReq extends RequestBase {

    private String groupId;

    private List<String> memberId;
}
