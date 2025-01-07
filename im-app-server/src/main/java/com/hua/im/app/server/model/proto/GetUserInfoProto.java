package com.hua.im.app.server.model.proto;
import java.util.List;
import lombok.Data;

@Data
public class GetUserInfoProto {

    private List<String> userIds;

    private List<String> standardField;

    private List<String> customField;

}
