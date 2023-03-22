package cn.edu.zjut.circleAndTopicService.model.vo;

import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author bert
 */
@Data
public class CircleVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前用户是否加入该圈子")
    private Boolean isFollow;

    @ApiModelProperty(value = "加入的前3个用户信息")
    private List<UserSimpleVo> joinUserList;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "圈主id")
    private Long creatorId;
    
    @ApiModelProperty(value = "圈子名称")
    private String name;
    
    @ApiModelProperty(value = "圈子头像")
    private String avatar;
    
    @ApiModelProperty(value = "一句话描述")
    private String resume;

    @ApiModelProperty(value = "详细描述")
    private String description;
    
    @ApiModelProperty(value = "活跃度")
    private Integer activityCount;

    @ApiModelProperty(value = "已加入用户数")
    private Integer joinCount;

    @ApiModelProperty(value = "动态数")
    private Integer postCount;

    @ApiModelProperty(value = "圈子类别id")
    private Integer categoryId;
    
    @ApiModelProperty(value = "创建时间")
    private Date createdTime;
}