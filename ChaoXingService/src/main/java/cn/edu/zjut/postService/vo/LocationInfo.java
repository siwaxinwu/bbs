package cn.edu.zjut.postService.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bert
 * @date 2023/1/20 21:49
 */
@ApiModel("位置签到信息")
@Data
public class LocationInfo {
    @ApiModelProperty("位置描述信息")
    private String locationText;
    @ApiModelProperty("纬度")
    private String locationLatitude;
    @ApiModelProperty("经度")
    private String locationLongitude;
}
