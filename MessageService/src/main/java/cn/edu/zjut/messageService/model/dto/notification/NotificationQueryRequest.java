package cn.edu.zjut.messageService.model.dto.notification;

import cn.edu.zjut.common.model.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class NotificationQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "通知类型")
    private String type;
}
