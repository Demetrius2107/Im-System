package com.hua.im.app.server.model.resp;

import java.util.Set;

import lombok.Data;

@Data
public class ImportUserResp {
    private Set<String> successId;

    private Set<String> errorId;
}
