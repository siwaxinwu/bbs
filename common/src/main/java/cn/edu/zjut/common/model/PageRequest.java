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
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前页")
    private int pageNum = 1;

    @ApiModelProperty(value = "页大小")
    private int pageSize = 10;

    public <T> Page<T> getPage(Class<T> tClass) {
        return new Page<T>(pageNum,pageSize);
    }
}