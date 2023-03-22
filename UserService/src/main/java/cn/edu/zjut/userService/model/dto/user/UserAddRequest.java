package cn.edu.zjut.userService.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author bert
 * @description 添加用户参数
 * @date 2023/1/10 21:54
 */
@Data
public class UserAddRequest implements Serializable {

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
     * 用户性别（0男 1女 2未知）
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


    private static final long serialVersionUID = 1L;
}
