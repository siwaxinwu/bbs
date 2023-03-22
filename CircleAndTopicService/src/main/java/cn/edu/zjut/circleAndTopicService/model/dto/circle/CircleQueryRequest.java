package cn.edu.zjut.circleAndTopicService.model.dto.circle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class CircleQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "标签名")
    private String name;

    @ApiModelProperty(value = "圈主id")
    private Long creatorId;
}
