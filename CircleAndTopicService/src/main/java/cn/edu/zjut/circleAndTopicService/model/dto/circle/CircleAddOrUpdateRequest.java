package cn.edu.zjut.circleAndTopicService.model.dto.circle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class CircleAddOrUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "圈子id")
    private Long id;

    @ApiModelProperty(value = "圈子名称")
    private String name;

    @ApiModelProperty(value = "圈主id")
    private Long creatorId;

    @ApiModelProperty(value = "圈子头像")
    private String avatar;

    @ApiModelProperty(value = "圈子描述")
    private String description;

    @ApiModelProperty(value = "圈子类别id")
    private Integer categoryId;
}
