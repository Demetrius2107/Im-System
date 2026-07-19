package com.lip.im.codec.pack.user;

import lombok.Data;

@Data
public class UserCustomStatusChangeNotifyPack {

    /** 自定义状态文本 */
    private String customText;

    /** 自定义状态码 */
    private Integer customStatus;

    /** 用户ID */
    private String userId;

}
