package cn.edu.zjut.userService.model.dto.userTag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @description 查找标签
 * @date 2023/1/12 11:37
 */
@Data
public class UserTagQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "标签名")
    private String tagName;

    @ApiModelProperty(value = "用户id")
    private Long userId;
}
