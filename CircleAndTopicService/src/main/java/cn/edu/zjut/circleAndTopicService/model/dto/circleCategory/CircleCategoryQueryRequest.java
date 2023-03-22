package cn.edu.zjut.circleAndTopicService.model.dto.circleCategory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class CircleCategoryQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "种类名")
    private String name;
}
