package cn.edu.zjut.common.utils;

import cn.edu.zjut.common.constants.PageConstants;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.PageRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author bert
 * @description 分页工具类
 * @date 2023/1/10 21:53
 */
public class PageUtils {

    public static void checkPageParam(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        if (pageRequest.getPageNum() <= 0) {
            pageRequest.setPageNum(1);
        }
        if (pageRequest.getPageSize() <= 0) {
            pageRequest.setPageSize(PageConstants.DEFAULT_PAGE_SIZE);
        }
        if (pageRequest.getPageNum() > PageConstants.MAX_PAGE_NUM) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
    }

    public static void checkPageParam(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageNum > PageConstants.MAX_PAGE_NUM) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = PageConstants.DEFAULT_PAGE_SIZE;
        }

    }


    public static <T> Page<T> getDefaultPage(Class<T> tClass) {
        return new Page<T>(1,PageConstants.DEFAULT_PAGE_SIZE);
    }


    public static  <T,Row> Page<T> getCopyPage(Page<Row> rowPage, List<T> data) {
        Page<T> tPage = new Page<>();
        tPage.setCurrent(rowPage.getCurrent());
        tPage.setSize(rowPage.getSize());
        tPage.setTotal(rowPage.getTotal());
        tPage.setRecords(data);
        return tPage;
    }
}
