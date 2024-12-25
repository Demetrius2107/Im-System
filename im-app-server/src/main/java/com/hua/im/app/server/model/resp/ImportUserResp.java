package com.hua.im.app.server.model.resp;

@Data
public class ImportUserResp {
    private Set<String> successId;

    private Set<String> errorId;
}
