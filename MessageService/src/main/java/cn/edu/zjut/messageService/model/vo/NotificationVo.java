package cn.edu.zjut.messageService.model.vo;

import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知表
 * @TableName notification
 */
@TableName(value ="notification")
@Data
public class NotificationVo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送用户id
     */
    private Long sendUserId;

    private UserSimpleVo user;

    /**
     * 接收用户id
     */
    private Long receiveUserId;

    /**
     * 消息类型，0系统，1评论，2回复，3点赞
     */
    private String type;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * url地址
     */
    private String url;

    /**
     * 资源id
     */
    private Long resourceId;

    /**
     * 封面
     */
    private String cover;

    /**
     * 是否已读，0未读，1已读
     */
    private Boolean isRead;

    private ResourceVo resource;

    /**
     * 创建时间
     */
    private Date createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}