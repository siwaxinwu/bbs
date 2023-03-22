package cn.edu.zjut.circleAndTopicService.model.dto.post;

import cn.edu.zjut.common.model.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class PostQueryByTopicRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "话题id")
    private Long topicId;

    @ApiModelProperty(value = "是否降序（默认降序）")
    private Boolean isDesc = true;
}
