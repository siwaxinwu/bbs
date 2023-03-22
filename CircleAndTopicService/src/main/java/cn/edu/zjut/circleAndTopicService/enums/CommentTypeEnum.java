package cn.edu.zjut.circleAndTopicService.enums;

import cn.edu.zjut.circleAndTopicService.model.entity.Comment;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author liyupi
 */
@AllArgsConstructor
@Getter
public enum CommentTypeEnum {

    /**
     * 普通评论
     */
    COMMENT("0"),
    /**
     * 回复
     */
    REPLY("1"),
    /**
     * 置顶
     */
    TOP("2")
    ;

    private final String value;
    public static CommentTypeEnum transform(String value) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getValue().equals(value)) {
                return commentTypeEnum;
            }
        }
        throw new BusinessException(CodeEnum.PARAMS_ERROR, "类型错误");
    }

}