package cn.edu.zjut.userService.model.dto.level;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class LevelAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "等级名")
    private String levelName;

    @ApiModelProperty(value = "所需经验值")
    private Integer count;
}
