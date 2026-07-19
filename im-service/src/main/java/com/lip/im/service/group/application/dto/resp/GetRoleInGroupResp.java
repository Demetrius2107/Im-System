package com.lip.im.service.group.application.dto.resp;

import lombok.Data;

/**
 * @author: Chackylee
 * @description:
 **/
@Data
public class GetRoleInGroupResp {

    private Long groupMemberId;

    private String memberId;

    private Integer role;

    private Long speakDate;

}
