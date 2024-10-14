package com.hua.im.imservice.user.model.req;

import com.hua.im.imcommon.model.RequestBase;
import com.hua.im.imservice.user.dao.ImUserDataEntity;
import lombok.Data;
import org.apache.catalina.connector.Request;

import java.util.List;

@Data
public class ImportUserReq extends RequestBase {

    private List<ImUserDataEntity> userData;
}
