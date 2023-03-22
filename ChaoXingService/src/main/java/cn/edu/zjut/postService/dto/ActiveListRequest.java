package cn.edu.zjut.postService.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bert
 * @date 2023/1/20 21:49
 */
@ApiModel("课程活动列表请求参数")
@Data
public class ActiveListRequest {

    @ApiModelProperty("课程id")
    private String courseId;
    @ApiModelProperty("班级id")
    private String classId;
}
