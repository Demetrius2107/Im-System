package com.hua.im.imcommon.enums;

/**
 * @author Shukun.Li
 */

public enum AllowFriendTypeEnum {

    /**
     * 需要验证
     */
    NEED(2),

    /**
     * 不需要验证
     */
    NOT_NEED(1);

    private int code;

    AllowFriendTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
