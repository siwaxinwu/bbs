package cn.edu.zjut.circleAndTopicService.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 动态表
 * @TableName post
 */
@TableName(value ="post")
@Data
public class Post implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 动态类型，00普通贴 01视频贴 02投票贴 03回复可见贴 04活动贴 05收费贴 90公告贴 
     */
    private String type;

    /**
     * 内容
     */
    private String content;

    /**
     * 圈子id
     */
    private Long circleId;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 获得金币数
     */
    private Integer coinCount;

    /**
     * 点赞数
     */
    private Integer thumbCount;

    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 是否精华贴,0否 1是
     */
    private Integer isEssence;

    /**
     * 是否置顶,0否 1是
     */
    private Integer isTop;

    /**
     * 状态,0审核中 1正常 2评论被锁定
     */
    private String status;

    /**
     * 计算分数权重，默认为1
     */
    private Double weight;

    /**
     * 是否被删除，0否 1是
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