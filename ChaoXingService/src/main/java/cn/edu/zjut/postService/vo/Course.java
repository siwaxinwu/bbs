package cn.edu.zjut.postService.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bert
 * @date 2023/1/20 23:13
 */
@ApiModel("课程")
@Data
public class Course {
    @ApiModelProperty("课程id")
    private String courseId;

    @ApiModelProperty("课程名")
    private String courseName;
    @ApiModelProperty("课程封面")
    private String courseCover;

    @ApiModelProperty("班级id")
    private String classId;
    @ApiModelProperty("班级名")
    private String className;

    @ApiModelProperty("教师名")
    private String teacherName;

}
