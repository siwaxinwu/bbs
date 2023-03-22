package cn.edu.zjut.fileService.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源表
 * @TableName resource
 */
@TableName(value ="resource")
@Data
public class Resource implements Serializable {
    /**
     * 资源id
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文件类型，0图片 1视频 2文件
     */
    private String type;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 状态，0正在审核 1审核通过 2审核不通过
     */
    private String status;

    /**
     * 真实地址
     */
    private String url;

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