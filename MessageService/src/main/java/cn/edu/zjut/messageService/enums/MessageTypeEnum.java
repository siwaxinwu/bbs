package cn.edu.zjut.messageService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author liyupi
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum {

    /**
     * 文字
     */
    TEXT("0"),
    /**
     * 图片
     */
    IMAGE("1"),
    ;

    private final String value;
    public static MessageTypeEnum transform(String value) {
        for (MessageTypeEnum commentTypeEnum : MessageTypeEnum.values()) {
            if (commentTypeEnum.getValue().equals(value)) {
                return commentTypeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

}