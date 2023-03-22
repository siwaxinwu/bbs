package cn.edu.zjut.messageService.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author bert
 * @date 2023/2/25 19:43
 */
@Data
public class NotificationItemVo {
    private Integer unReadCount;
    private String title;
    private Date date;
}
