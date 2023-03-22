package cn.edu.zjut.common.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 * @author liyupi
 */
@Data
public class ScoreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "最小值")
    private Long min = 0L;

    @ApiModelProperty(value = "最大值")
    private Long max;
}