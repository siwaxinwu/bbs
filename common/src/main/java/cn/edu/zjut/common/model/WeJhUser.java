package cn.edu.zjut.common.model;

import lombok.Data;

import java.util.Date;

/**
 * @author bert
 * @date 2023/1/20 22:55
 */
@Data
public class WeJhUser {
    private Long id;
    private String phoneNum;
    private String studentID;
    private String username;
    private Integer userType;
    private Date createTime;
}
