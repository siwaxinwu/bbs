package cn.edu.zjut.circleAndTopicService.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 评论表
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 评论类型，0评论 1回复 2置顶评论
     */
    private String type;

    /**
     * 回复的评论id (type=1时有效)
     */
    private Long replyCommentId;

    /**
     * 回复的用户id (type=1时有效)
     */
    private Long replyUserId;

    /**
     * 动态id
     */
    private Long postId;

    /**
     * 父级评论id（type=1时有效）
     */
    private Long commentId;

    /**
     * 评论ip
     */
    private String ip;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 点赞数
     */
    private Integer thumbCount;

    /**
     * 是否删除（0正常 1删除）
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}