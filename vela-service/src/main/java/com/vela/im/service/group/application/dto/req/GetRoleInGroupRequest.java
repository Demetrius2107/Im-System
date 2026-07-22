package com.vela.im.service.group.application.dto.req;


import com.vela.im.shared.types.RequestBase;
import lombok.Data;

import java.util.List;

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
 */
@Data
public class GetRoleInGroupRequest extends RequestBase {

    private String groupId;

    private List<String> memberIds;
}
