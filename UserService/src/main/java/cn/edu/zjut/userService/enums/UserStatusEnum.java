package cn.edu.zjut.userService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;

public enum UserStatusEnum {

    /**
     * 正常
     */
    NORMAL("0"),
    /**
     * 停用
     */
    BLOCKED("1"),
    /**
     * 未激活
     */
    NOT_ACTIVE("2"),
    ;

    private final String value;

    UserStatusEnum(String value) {
        this.value = value;
    }

    public static UserStatusEnum transform(String value) {
        for (UserStatusEnum typeEnum : UserStatusEnum.values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

    public String getValue() {
        return value;
    }
}
