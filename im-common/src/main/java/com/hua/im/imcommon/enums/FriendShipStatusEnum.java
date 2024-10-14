package com.hua.im.imcommon.enums;

public enum FriendShipStatusEnum {

    /**
     * 0未添加 1正常 2删除
     */
    FRIEND_STATUS_NO_FRIEND(0),
    FRIEND_STATUS_NORMAL(1),
    FRIEND_STATUS_DELETE(2),

    /**
     *
     */
    BLACK_STATUS_NORMAL(1),
    BLACK_STATUS_BALCKED(2),
    ;

    private int code;

    FriendShipStatusEnum(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}