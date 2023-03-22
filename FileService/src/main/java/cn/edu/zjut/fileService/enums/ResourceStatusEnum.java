package cn.edu.zjut.fileService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源状态枚举
 *
 * @author liyupi
 */
@AllArgsConstructor
@Getter
public enum ResourceStatusEnum {

    /**
     * 正在审核
     */
    UNDER_REVIEW("0"),
    /**
     * 审核通过
     */
    APPROVED("1"),
    /**
     * 审核不通过
     */
    NOT_APPROVED("1"),
    ;

    private final String value;
    public static ResourceStatusEnum transform(String value) {
        for (ResourceStatusEnum commentTypeEnum : ResourceStatusEnum.values()) {
            if (commentTypeEnum.getValue().equals(value)) {
                return commentTypeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

}