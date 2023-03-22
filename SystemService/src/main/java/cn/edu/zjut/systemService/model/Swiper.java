package cn.edu.zjut.systemService.model;

import lombok.Data;

/**
 * @author bert
 * @date 2023/3/5 16:50
 */
@Data
public class Swiper {
    private String type;
    private Long resourceId;
    private String url;
    private String cover;
    private String title;
}
