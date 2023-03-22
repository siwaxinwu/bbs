package cn.edu.zjut.userService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;

public enum SexEnum {

    /**
     * 未知
     */
    NO_KNOW("0"),
    /**
     * 男
     */
    MAN("1"),
    /**
     * 女
     */
    WOMAN("2"),
    ;

    private final String value;

    SexEnum(String value) {
        this.value = value;
    }

    public static SexEnum transform(String value) {
        for (SexEnum commentTypeEnum : SexEnum.values()) {
            if (commentTypeEnum.getValue().equals(value)) {
                return commentTypeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

    public String getValue() {
        return value;
    }
}
