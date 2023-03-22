package cn.edu.zjut.circleAndTopicService.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 话题表
 * @TableName topic
 */
@TableName(value ="topic")
@Data
public class Topic implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 话题名
     */
    private String name;

    /**
     * 创建用户id
     */
    private Long creatorId;

    /**
     * 话题描述
     */
    private String description;

    /**
     * 动态数
     */
    private Integer postCount;

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