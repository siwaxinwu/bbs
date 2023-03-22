package cn.edu.zjut.userService.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@Data
public class User implements Serializable {
    /**
     * uid
     */
    @TableId(type = IdType.AUTO)
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
     * 密码
     */
    private String password;

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
     * 连续签到天数
     */
    private Integer signCount;

    /**
     * 是否删除（0正常 1删除）
     */
    @TableLogic
    @TableField("is_delete")
    private Boolean deleteBool;

    /**
     * 上次登录ip
     */
    private String loginIp;

    /**
     * 上次登录时间
     */
    private Date loginDate;

    /**
     * 超星id
     */
    private Long cxId;

    /**
     * 微精弘id
     */
    private Long wejhId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}