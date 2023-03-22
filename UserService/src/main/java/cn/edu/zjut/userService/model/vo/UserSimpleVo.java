package cn.edu.zjut.userService.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @author bert
 * @TableName user
 */
@Data
public class UserSimpleVo implements Serializable {
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
     * 用户性别（0未知 1男 2女）
     */
    private String gender;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * token (用于登录时返回)
     */
    private String token;

    private static final long serialVersionUID = 1L;
}