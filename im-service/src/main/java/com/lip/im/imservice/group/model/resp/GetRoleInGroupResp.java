package com.lip.im.imservice.group.model.resp;

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
