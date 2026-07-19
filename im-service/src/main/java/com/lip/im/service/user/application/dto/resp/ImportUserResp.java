package com.lip.im.service.user.application.dto.resp;

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
