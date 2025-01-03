package com.hua.im.imservice.user.model.req;

import com.hua.im.imcommon.model.RequestBase;
import com.hua.im.imservice.user.dao.ImUserDataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.catalina.connector.Request;

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
