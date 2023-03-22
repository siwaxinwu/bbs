package cn.edu.zjut.circleAndTopicService.model.dto.post;

import cn.edu.zjut.common.model.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Set;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "圈子id")
    private Long circleId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "动态类型，00普通贴 01视频贴 02投票贴 03回复可见贴 04活动贴 05收费贴 90公告贴")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "是否精华贴,0否 1是")
    private Integer isEssence;

    @ApiModelProperty(value = "状态,0审核中 1正常 2评论被锁定")
    private String status;

    @ApiModelProperty(value = "是否为降序，默认降序")
    private Boolean isDesc = true;
}
