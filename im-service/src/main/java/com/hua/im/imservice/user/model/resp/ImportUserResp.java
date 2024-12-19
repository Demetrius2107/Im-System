package com.hua.im.imservice.user.model.resp;

import lombok.Data;

import java.util.List;

/**
 * @author Shukun.Li
 */
@Data
public class ImportUserResp {

    private List<String> successId;

    private List<String> errorId;
}
