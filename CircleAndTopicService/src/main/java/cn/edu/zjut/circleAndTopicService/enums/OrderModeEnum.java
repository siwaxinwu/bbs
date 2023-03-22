package cn.edu.zjut.circleAndTopicService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author liyupi
 */
@AllArgsConstructor
@Getter
public enum OrderModeEnum {

    /**
     * 最热
     */
    HOT(0),
    /**
     * 最新
     */
    NEW(1),
    /**
     * 最旧
     */
    OLD(2)
    ;

    private final Integer value;
    public static OrderModeEnum transform(Integer value) {
        for (OrderModeEnum typeEnum : OrderModeEnum.values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

}