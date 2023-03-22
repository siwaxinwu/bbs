package cn.edu.zjut.messageService.model.dto.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class MessageAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "receiveUserId")
    private Long receiveUserId;

    @ApiModelProperty(value = "消息类型")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

}
