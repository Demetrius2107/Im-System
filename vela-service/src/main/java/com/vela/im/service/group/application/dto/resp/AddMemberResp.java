package com.vela.im.service.group.application.dto.resp;

import lombok.Data;

/**
 * @description:
 * @author wanqiu
 * @version: 1.0
 */
@Data
public class AddMemberResp {

    private String memberId;

    // 加人结果：0 为成功；1 为失败；2 为已经是群成员
    private Integer result;

    private String resultMessage;
}
