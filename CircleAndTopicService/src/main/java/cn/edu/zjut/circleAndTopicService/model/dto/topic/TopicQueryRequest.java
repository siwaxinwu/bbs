package cn.edu.zjut.circleAndTopicService.model.dto.topic;

import cn.edu.zjut.common.model.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class TopicQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "话题名")
    private String name;

    @ApiModelProperty(value = "创建用户id")
    private Long creatorId;

    @ApiModelProperty(value = "动态数")
    private Integer postCount;
}
