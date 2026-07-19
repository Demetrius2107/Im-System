package com.vela.im.service.group.application.dto.callback;

import com.vela.im.service.group.application.dto.resp.AddMemberResp;
import lombok.Data;

import java.util.List;

@Data
public class AddMemberAfterCallback {
    private String groupId;
    private Integer groupType;
    private String operater;
    private List<AddMemberResp> memberId;
}
