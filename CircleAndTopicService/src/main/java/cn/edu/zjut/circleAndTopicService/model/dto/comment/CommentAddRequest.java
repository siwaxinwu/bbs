package cn.edu.zjut.circleAndTopicService.model.dto.comment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class CommentAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "评论类型，0评论 1回复 2置顶评论")
    private String type;

    @ApiModelProperty(value = "回复的评论id (type=1时有效)")
    private Long replyCommentId;

    @ApiModelProperty(value = "父级评论id（type=1时有效）")
    private Long commentId;

    @ApiModelProperty(value = "回复的用户id (type=1时有效)")
    private Long replyUserId;

    @ApiModelProperty(value = "动态id")
    private Long postId;


}
