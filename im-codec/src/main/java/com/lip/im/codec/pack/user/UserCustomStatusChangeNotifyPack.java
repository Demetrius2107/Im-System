package com.lip.im.codec.pack.user;

import lombok.Data;

@Data
public class UserCustomStatusChangeNotifyPack {

    private String customText;

    private Integer customStatus;

    private String userId;

}
