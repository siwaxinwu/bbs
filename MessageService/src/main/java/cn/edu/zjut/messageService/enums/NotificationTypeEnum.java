package cn.edu.zjut.messageService.enums;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知类型枚举
 *
 * @author liyupi
 */
@AllArgsConstructor
@Getter
public enum NotificationTypeEnum {

    /**
     * 系统
     */
    SYSTEM("0"),
    /**
     * 评论
     */
    COMMENT("1"),
    /**
     * 回复
     */
    REPLY("2"),
    /**
     * 点赞
     */
    THUMB("3"),
    ;

    private final String value;
    public static NotificationTypeEnum transform(String value) {
        for (NotificationTypeEnum commentTypeEnum : NotificationTypeEnum.values()) {
            if (commentTypeEnum.getValue().equals(value)) {
                return commentTypeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

}