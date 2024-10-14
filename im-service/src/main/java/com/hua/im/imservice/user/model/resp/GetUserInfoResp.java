package com.hua.im.imservice.user.model.resp;

import com.hua.im.imservice.user.dao.ImUserDataEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetUserInfoResp {

    private List<ImUserDataEntity> userDataItem;

    private List<String> failUser;

}
