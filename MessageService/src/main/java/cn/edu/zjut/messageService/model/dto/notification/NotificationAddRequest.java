package cn.edu.zjut.messageService.model.dto.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class NotificationAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "接收用户id")
    private Long receiveUserId;

    @ApiModelProperty(value = "通知类型")
    private String type;

    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "链接")
    private String url;
    @ApiModelProperty(value = "资源id")
    private Long resourceId;

}
