package cn.edu.zjut.common.model;

import lombok.Data;

import java.util.Date;

/**
 * @author bert
 * @date 2023/1/20 22:55
 */
@Data
public class ChaoXingUser {
    private Integer fid;
    private String schoolName;
    private String xuehao;
    private String name;
    private String phone;
    private Long cxId;
    private Date acTime;
    private String idCard;

}
