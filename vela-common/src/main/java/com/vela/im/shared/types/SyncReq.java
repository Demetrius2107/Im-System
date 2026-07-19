package com.vela.im.shared.types;

import lombok.Data;

/**
 * <p>Title: SyncReq</p>
 * <p>Description: 增量同步请求，继承 RequestBase，携带客户端最大序列号和拉取数量限制。</p>
 * <p>项目名称: IM-System</p>
 *
 * @author wanqiu
 * @since 1.0
 * @createTime 2025-03-03
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 */
@Data
public class SyncReq extends RequestBase {

    /** 客户端最大序列号，服务端返回大于此seq的数据 */
    private Long lastSequence;

    /** 单次拉取数量上限 */
    private Integer maxLimit;

}
