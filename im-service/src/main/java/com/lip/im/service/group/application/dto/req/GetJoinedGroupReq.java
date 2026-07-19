package com.lip.im.service.group.application.dto.req;


import com.lip.im.shared.types.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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
 */@Data
public class GetJoinedGroupReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String memberId;

    //群类型
    private List<Integer> groupType;

    //单次拉取的群组数量，如果不填代表所有群组
    private Integer limit;

    //第几页
    private Integer offset;


}
