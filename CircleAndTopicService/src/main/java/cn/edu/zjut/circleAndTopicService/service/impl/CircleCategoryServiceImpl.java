package cn.edu.zjut.circleAndTopicService.service.impl;

import cn.edu.zjut.circleAndTopicService.mapper.CircleMapper;
import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.circleAndTopicService.model.entity.CircleCategory;
import cn.edu.zjut.circleAndTopicService.service.CircleCategoryService;
import cn.edu.zjut.circleAndTopicService.mapper.CircleCategoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author bert
* @description 针对表【circle_category(圈子种类表)】的数据库操作Service实现
* @createDate 2023-01-13 21:20:53
*/
@Service
public class CircleCategoryServiceImpl extends ServiceImpl<CircleCategoryMapper, CircleCategory>
    implements CircleCategoryService{

    @Resource
    private CircleMapper circleMapper;

    @Override
    public List<Circle> getCircleListByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Circle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Circle::getCategoryId,categoryId);
        return circleMapper.selectList(wrapper);
    }
}




