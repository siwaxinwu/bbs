package cn.edu.zjut.userService.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 积分记录表
 * @author bert
 * @TableName coin_record
 */
@TableName(value ="coin_record")
@Data
public class CoinRecord implements Serializable {
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
     * 资源id
     */
    private Long resourceId;

    /**
     * 资源类型，0帖子，1评论
     */
    private String resourceType;

    /**
     * 操作数
     */
    private Integer count;

    /**
     * 操作类型，0为加，1为减
     */
    private Integer operationType;

    /**
     * 剩余数
     */
    private Integer remain;

    /**
     * 备注
     */
    private String note;

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