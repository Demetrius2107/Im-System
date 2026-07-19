package com.lip.im.service.user.application.dto.resp;

import com.lip.im.service.user.domain.entity.ImUserDataEntity;
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
