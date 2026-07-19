package com.vela.im.service.user.application.dto.req;

import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import com.vela.im.shared.types.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author wanqiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImportUserReq extends RequestBase {

    /**
     * 用户数据集合
     */
    private List<ImUserDataEntity> userData;
}
