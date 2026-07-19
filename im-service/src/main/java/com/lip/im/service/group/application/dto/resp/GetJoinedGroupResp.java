package com.lip.im.service.group.application.dto.resp;


import com.lip.im.service.group.domain.entity.ImGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 * @description:
 **/
@Data
public class GetJoinedGroupResp {

    private Integer totalCount;

    private List<ImGroupEntity> groupList;

}
