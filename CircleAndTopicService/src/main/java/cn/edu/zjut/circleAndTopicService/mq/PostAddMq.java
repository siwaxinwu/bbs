package cn.edu.zjut.circleAndTopicService.mq;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class PostAddMq implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long postId;
    private Long postUserId;

    @ApiModelProperty(value = "圈子id")
    private Long circleId;

    @ApiModelProperty(value = "话题ids")
    private Set<Long> topicIds;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "动态类型")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

}
