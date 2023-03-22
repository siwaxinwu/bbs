package cn.edu.zjut.schoolToolService.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author bert
 * @date 2023/3/2 16:26
 */
@ApiModel("获取正方上数据的参数")
@Data
public class ZfRequest {
    private String year;
    private String term;
    private String period;
}
