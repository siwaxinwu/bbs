package cn.edu.zjut.postService.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bert
 * @date 2023/1/20 21:49
 */
@ApiModel("签到请求参数")
@Data
public class SignRequest {

    @ApiModelProperty("签到类型：42普通签到")
    private Integer typeCode;

    @ApiModelProperty("课程id")
    private String courseId;
    @ApiModelProperty("班级id")
    private Integer classId;
    @ApiModelProperty("活动id")
    private String activeId;

    @ApiModelProperty("位置描述信息")
    private String locationText;
    @ApiModelProperty("纬度")
    private String locationLatitude;
    @ApiModelProperty("经度")
    private String locationLongitude;
}
