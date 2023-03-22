package cn.edu.zjut.postService.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bert
 * @date 2023/1/20 23:13
 */
@ApiModel("活动")
@Data
public class Active {
    @ApiModelProperty("id")
    private String activeId;

    @ApiModelProperty("活动类型：2签到 45通知 42随堂练习 60教案")
    private Integer type;
    @ApiModelProperty("status=1未结束 2已结束")
    private Integer status;
    @ApiModelProperty("是否已阅")
    private Integer isLook;

    @ApiModelProperty("通知名")
    private String name;
    @ApiModelProperty("logo")
    private String logo;
    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("结束时间格式化")
    private String endTimeFriendly;
}
