package cn.edu.zjut.messageService.model.vo;

import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息表
 * @TableName message
 */
@TableName(value ="message")
@Data
public class MessageVo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送用户id
     */
    private Long sendUserId;

    /**
     * 用户
     */
    private UserSimpleVo user;

    /**
     * 接收用户id
     */
    private Long receiveUserId;

    /**
     * 消息类型，0文字，1图片
     */
    private String type;

    /**
     * 是否已读，0未读，1已读
     */
    private Boolean isRead;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}