package cn.edu.zjut.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user
 */
@Data
public class UserDto implements Serializable {

    private Long userId;
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户类型（00超级管理员 01系统管理员 10学生 20教师 90游客）
     */
    private String userType;

    /**
     * 用户性别（0未知 1男 2女）
     */
    private String gender;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 故乡
     */
    private String hometown;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个性签名
     */
    private String sign;

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

    /**
     * 金币数
     */
    private Integer coinCount;

    /**
     * 经验数
     */
    private Integer levelCount;

    /**
     * 动态数
     */
    private Integer postCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 粉丝数
     */
    private Integer fanCount;

    /**
     * 关注数
     */
    private Integer followCount;

    /**
     * 上次登录ip
     */
    private String loginIp;

    /**
     * 上次登录时间
     */
    private Date loginDate;

    /**
     * 创建时间
     */
    private Date createdTime;


    private static final long serialVersionUID = 1L;
}