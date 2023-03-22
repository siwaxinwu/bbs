package cn.edu.zjut.circleAndTopicService.model.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class PostAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "圈子id")
    private Long circleId;

    @ApiModelProperty(value = "话题ids")
    private Set<Long> topicIds;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "动态类型，00普通贴 01视频贴 02投票贴 03回复可见贴 04活动贴 05收费贴 90公告贴")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

}
