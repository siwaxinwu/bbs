package cn.edu.zjut.circleAndTopicService.service;

import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import cn.edu.zjut.circleAndTopicService.model.entity.CircleCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author bert
* @description 针对表【circle_category(圈子种类表)】的数据库操作Service
* @createDate 2023-01-13 21:20:53
*/
public interface CircleCategoryService extends IService<CircleCategory> {

    /**
     * 获取某种类下的所有圈子
     * @param categoryId categoryId
     * @return List<Circle>
     */
    List<Circle> getCircleListByCategoryId(Long categoryId);
}
