package cn.edu.zjut.userService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;

public enum CoinOperationTypeEnum {

    /**
     * 正常
     */
    ADD(0),
    /**
     * 停用
     */
    MINUS(1);

    private final Integer value;

    CoinOperationTypeEnum(Integer value) {
        this.value = value;
    }

    public static CoinOperationTypeEnum transform(Integer value) {
        for (CoinOperationTypeEnum commentTypeEnum : CoinOperationTypeEnum.values()) {
            if (commentTypeEnum.getValue().equals(value)) {
                return commentTypeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

    public Integer getValue() {
        return value;
    }
}
