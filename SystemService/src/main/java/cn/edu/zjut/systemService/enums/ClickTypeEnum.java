package cn.edu.zjut.systemService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;

/**
 * @author bert
 */

public enum ClickTypeEnum {

    /**
     * 帖子
     */
    POST("0"),
    /**
     * 链接
     */
    URL("1"),
    ;

    private final String value;

    ClickTypeEnum(String value) {
        this.value = value;
    }

    public static ClickTypeEnum transform(String value) {
        for (ClickTypeEnum typeEnum : ClickTypeEnum.values()) {
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
