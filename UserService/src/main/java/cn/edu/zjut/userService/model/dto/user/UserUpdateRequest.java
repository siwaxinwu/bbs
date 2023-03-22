package cn.edu.zjut.userService.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author bert
 * @description 添加用户参数
 * @date 2023/1/10 21:54
 */
@Data
public class UserUpdateRequest implements Serializable {

    private Long userId;
    private String nickName;
    private String gender;
//    private String avatar;
    private String avatarId;
    private Date birthday;
    private String hometown;
    private String sign;
    private String college;
    private String major;
    private Integer grade;

    private static final long serialVersionUID = 1L;
}
