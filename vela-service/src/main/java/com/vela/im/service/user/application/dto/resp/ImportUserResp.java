package com.vela.im.service.user.application.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * @author wanqiu
 */
@Data
public class ImportUserResp {

    private List<String> successId;

    private List<String> errorId;
}
