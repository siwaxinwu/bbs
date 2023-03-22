package cn.edu.zjut.circleAndTopicService.model.vo;

import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import cn.edu.zjut.circleAndTopicService.model.entity.Topic;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * postVo
 */
@Data
public class PostVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户信息")
    private UserSimpleVo user;

    @ApiModelProperty(value = "是否已点过赞")
    private Boolean isThumb = false;

    @ApiModelProperty(value = "圈子信息")
    private Circle circle;

    @ApiModelProperty(value = "话题列表")
    private List<Topic> topicList;

    @ApiModelProperty(value = "图片列表")
    private List<String> imageList;

    @ApiModelProperty(value = "热门评论")
    private List<CommentVo> hotCommentList;

    // region 本表信息

    @ApiModelProperty(value = "postId")
    private Long id;

    @ApiModelProperty(value = "动态类型，00普通贴 01视频贴 02投票贴 03回复可见贴 04活动贴 05收费贴 90公告贴")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

    @ApiModelProperty(value = "获得金币数")
    private Integer coinCount;

    @ApiModelProperty(value = "点赞数")
    private Integer thumbCount;

    @ApiModelProperty(value = "阅读量")
    private Integer readCount;

    @ApiModelProperty(value = "是否精华贴,0否 1是")
    private Integer isEssence;

    @ApiModelProperty(value = "是否置顶,0否 1是")
    private Integer isTop;

    @ApiModelProperty(value = "状态,0审核中 1正常 2评论被锁定")
    private String status;
    
    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    // endregion 本表信息

}