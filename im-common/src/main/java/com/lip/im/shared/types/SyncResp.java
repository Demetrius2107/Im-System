package com.lip.im.shared.types;

import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 * @description:
 **/
@Data
public class SyncResp<T> {

    private Long maxSequence;

    private boolean isCompleted;

    private List<T> dataList;

}
