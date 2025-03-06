package com.lip.im.imservice.user.model.req;

import com.lip.im.imservice.user.dao.ImUserDataEntity;
import com.lip.im.model.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Shukun.Li
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImportUserReq extends RequestBase {

    /**
     * 用户数据集合
     */
    private List<ImUserDataEntity> userData;
}
