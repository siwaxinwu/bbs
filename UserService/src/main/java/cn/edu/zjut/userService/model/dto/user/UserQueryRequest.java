package cn.edu.zjut.userService.model.dto.user;

import cn.edu.zjut.common.model.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bert
 * @description 查询用户
 * @date 2023/1/10 21:07
 */
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * uid
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 用户类型（00超级管理员 01系统管理员 10学生 20教师 90游客）
     */
    private String userType;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String gender;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 学院名
     */
    private String college;

    /**
     * 专业名
     */
    private String major;

    /**
     * 年级
     */
    private Integer grade;

    private static final long serialVersionUID = 1L;
}
