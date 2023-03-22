package cn.edu.zjut.circleAndTopicService.model.dto.topic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class TopicAddOrUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "话题名")
    private String name;

    @ApiModelProperty(value = "话题描述")
    private String description;

}
