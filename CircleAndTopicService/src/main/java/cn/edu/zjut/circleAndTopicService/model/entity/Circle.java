package cn.edu.zjut.circleAndTopicService.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 圈子表
 * @author bert
 * @TableName circle
 */
@TableName(value ="circle")
@Data
public class Circle implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 圈主id
     */
    private Long creatorId;

    /**
     * 圈子名称
     */
    private String name;

    /**
     * 圈子头像
     */
    private String avatar;

    /**
     * 一句话描述
     */
    private String resume;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 活跃度
     */
    private Integer activityCount;

    /**
     * 已加入用户数
     */
    private Integer joinCount;

    /**
     * 动态数
     */
    private Integer postCount;

    /**
     * 圈子类别id
     */
    private Integer categoryId;

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