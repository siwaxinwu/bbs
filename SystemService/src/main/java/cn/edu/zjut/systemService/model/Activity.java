package cn.edu.zjut.systemService.model;

import lombok.Data;

import java.util.Date;

/**
 * @author bert
 * @date 2023/3/5 17:00
 */
@Data
public class Activity {
    private String cover;
    private String title;
    private String url;
    private String beginTime;
    private String endTime;
    private String type;
    private Long resourceId;
}
