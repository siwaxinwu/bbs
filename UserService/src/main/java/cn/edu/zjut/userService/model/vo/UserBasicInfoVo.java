package cn.edu.zjut.userService.model.vo;

import cn.edu.zjut.userService.model.entity.UserTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description 用户基础信息
 * @author bert
 * @date 2023/1/9 22:56
 */
@Data
public class UserBasicInfoVo implements Serializable {

    @ApiModelProperty(value = "uid")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "用户类型（00超级管理员 01系统管理员 10学生 20教师 90游客）")
    private String userType;

    @ApiModelProperty(value = "用户性别（0未知 1男 2女）")
    private String gender;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "生日")
    private Date birthday;

    @ApiModelProperty(value = "故乡")
    private String hometown;

    @ApiModelProperty(value = "个性签名")
    private String sign;

    @ApiModelProperty(value = "学院名")
    private String college;

    @ApiModelProperty(value = "专业名")
    private String major;

    @ApiModelProperty(value = "年级")
    private Integer grade;

    @ApiModelProperty(value = "金币数")
    private Integer coinCount;

    @ApiModelProperty(value = "经验数")
    private Integer levelCount;

    @ApiModelProperty(value = "等级名")
    private String levelName;

    @ApiModelProperty(value = "动态数")
    private Integer postCount;

    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "粉丝数")
    private Integer fanCount;

    @ApiModelProperty(value = "关注数")
    private Integer followCount;

    @ApiModelProperty(value = "签到数")
    private Integer signCount;

    @ApiModelProperty(value = "上次登录时间")
    private Date loginDate;

    @ApiModelProperty(value = "注册时间")
    private Date createdTime;

    @ApiModelProperty(value = "是否被当前用户关注")
    private Boolean isFollow;

    @ApiModelProperty(value = "用户标签列表")
    private List<UserTag> tags;

    private static final long serialVersionUID = 1L;
}
