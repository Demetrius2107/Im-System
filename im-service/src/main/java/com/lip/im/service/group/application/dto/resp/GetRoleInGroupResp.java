package com.lip.im.service.group.application.dto.resp;

import lombok.Data;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-06
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */@Data
public class GetRoleInGroupResp {

    private Long groupMemberId;

    private String memberId;

    private Integer role;

    private Long speakDate;

}
