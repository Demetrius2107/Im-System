package com.lip.im.service.friendship.application.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * @author Shukun.Li
 */
@Data
public class ImportFriendShipResp {

    private List<String> successId;

    private List<String> errorId;
}
