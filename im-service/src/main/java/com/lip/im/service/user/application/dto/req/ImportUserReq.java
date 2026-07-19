package com.lip.im.service.user.application.dto.req;

import com.lip.im.service.user.domain.entity.ImUserDataEntity;
import com.lip.im.shared.types.RequestBase;
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
