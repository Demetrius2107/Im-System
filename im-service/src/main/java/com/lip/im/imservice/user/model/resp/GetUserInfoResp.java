package com.lip.im.imservice.user.model.resp;

import com.lip.im.imservice.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Shukun.Li
 */
@Data
public class GetUserInfoResp {

    private List<ImUserDataEntity> userDataItem;

    private List<String> failUser;

}
