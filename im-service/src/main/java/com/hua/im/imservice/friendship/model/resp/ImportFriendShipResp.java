package com.hua.im.imservice.friendship.model.resp;

import lombok.Data;

import java.util.List;

@Data
public class ImportFriendShipResp {

    private List<String> successId;

    private List<String> errorId;
}