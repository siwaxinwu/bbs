package cn.edu.zjut.common.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author bert
 */
@Data
public class ScrollResult<T> {
    private List<T> list;
    private Long minTime;
    private Integer offset;

    public static <T> ScrollResult<T> empty(long minTime, int offset) {
        ScrollResult<T> result = new ScrollResult<T>();
        result.setMinTime(minTime);
        result.setOffset(offset);
        result.setList(Collections.emptyList());
        return result;
    }
}