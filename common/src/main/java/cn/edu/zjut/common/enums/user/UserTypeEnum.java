package cn.edu.zjut.common.enums.user;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;

/**
 * @author bert
 */

public enum UserTypeEnum {

    /**
     * 管理员
     */
    ADMIN("0"),
    /**
     * 学生
     */
    STUDENT("1"),
    /**
     * 教师
     */
    TEACHER("2"),
    ;

    private final String value;

    UserTypeEnum(String value) {
        this.value = value;
    }

    public static UserTypeEnum transform(String value) {
        for (UserTypeEnum typeEnum : UserTypeEnum.values()) {
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
