package cn.edu.zjut.circleAndTopicService.model.vo;

import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论vo
 * @author bert
 */
@Data
public class CommentVo implements Serializable {

    @ApiModelProperty(value = "评论id")
    private Long id;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "用户")
    private UserSimpleVo user;

    @ApiModelProperty(value = "是否已点过赞")
    private Boolean isThumb = false;

    @ApiModelProperty(value = "被回复用户")
    private UserSimpleVo replyUser;

    @ApiModelProperty(value = "评论类型，0评论 1回复 2置顶评论")
    private String type;

    @ApiModelProperty(value = "回复的评论(type=1时有效)")
    private CommentVo parentComment;

    @ApiModelProperty(value = "父级评论")
    private Long commentId;

    @ApiModelProperty(value = "热门回复")
    private List<CommentVo> hotReplyList;
    
    @ApiModelProperty(value = "动态id")
    private Long postId;

    @ApiModelProperty(value = "评论ip属地")
    private String ipAddress;

    @ApiModelProperty(value = "回复数")
    private Integer replyCount;

    @ApiModelProperty(value = "点赞数")
    private Integer thumbCount;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;
}