package com.vela.im.service.user.application.dto.resp;

import com.vela.im.service.user.domain.entity.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 */
@Data
public class GetUserInfoResp {

    private List<ImUserDataEntity> userDataItem;

    private List<String> failUser;

}
