package cn.edu.zjut.userService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;

public enum CoinRecordNoteEnum {

    /**
     * 签到
     */
    SIGN("签到"),
    /**
     * 用户赠送
     */
    USER_SEND("用户赠送"),
    /**
     * 系统增送
     */
    SYSTEM_ADD("系统增送"),
    /**
     * 系统扣除
     */
    SYSTEM_MIN("系统扣除"),
    ;

    private final String value;

    CoinRecordNoteEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
