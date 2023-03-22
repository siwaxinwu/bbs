package cn.edu.zjut.messageService.model.dto.message;

import cn.edu.zjut.common.model.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @date 2023/1/12 11:37
 */
@Data
public class MessageQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "sendUserId")
    private Long sendUserId;
    @ApiModelProperty(value = "receiveUserId")
    private Long receiveUserId;
}
