package com.hua.im.imcommon.enums;

/**
 * @author Shukun.Li
 */

public enum DelFlagEnum {

    NORMAL(0),
    DELETE(1),
    ;

    private int code;

    DelFlagEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
