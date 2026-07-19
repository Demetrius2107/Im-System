package com.vela.im.shared.types;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: SyncResp</p>
 * <p>Description: 增量同步响应，包含最大序列号、是否拉取完毕和数据列表。</p>
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
public class SyncResp<T> {

    /** 服务端最大序列号 */
    private Long maxSequence;

    /** 是否已拉取完毕 */
    private boolean isCompleted;

    /** 同步数据列表 */
    private List<T> dataList;

}
