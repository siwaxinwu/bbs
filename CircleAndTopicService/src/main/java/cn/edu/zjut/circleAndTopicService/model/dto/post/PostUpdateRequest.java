package cn.edu.zjut.circleAndTopicService.model.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class PostUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "动态id")
    private Long id;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "是否精华贴,0否 1是")
    private Integer isEssence;

    @ApiModelProperty(value = "是否置顶,0否 1是")
    private Integer isTop;

    @ApiModelProperty(value = "状态,0审核中 1正常 2评论被锁定")
    private String status;

}
