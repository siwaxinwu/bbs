package cn.edu.zjut.circleAndTopicService.controller;

import cn.edu.zjut.circleAndTopicService.model.dto.circleCategory.CircleCategoryAddOrUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.circleCategory.CircleCategoryQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import cn.edu.zjut.circleAndTopicService.model.entity.CircleCategory;
import cn.edu.zjut.circleAndTopicService.service.CircleCategoryService;
import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bert
 * @description 圈子种类 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("circleCategory")
@Api(tags = "圈子种类")
public class CircleCatrgoryController {

    @Resource
    private CircleCategoryService circleCategoryService;

    // region 增删改查

    @PostMapping("/list")
    @ApiOperation("查询")
    public Result<List<CircleCategory>> getCircleCategoryList(@RequestBody CircleCategoryQueryRequest circleCategoryQueryRequest) {
        LambdaQueryWrapper<CircleCategory> wrapper = getQueryWrapper(circleCategoryQueryRequest);
        return Result.ok(circleCategoryService.list(wrapper));
    }


    @PostMapping
    @ApiOperation("添加圈子种类")
    @RequireAdmin
    public Result<Boolean> addCircleCategory(@RequestBody CircleCategoryAddOrUpdateRequest request) {
        CircleCategory circleCategory = new CircleCategory();
        circleCategory.setName(request.getName());
        return Result.bool(circleCategoryService.save(circleCategory));
    }

    @PutMapping
    @ApiOperation("更新圈子种类")
    @RequireAdmin
    public Result<Boolean> updateCircleCategory(@RequestBody CircleCategoryAddOrUpdateRequest request) {
        CircleCategory circleCategory = new CircleCategory();
        circleCategory.setId(request.getId());
        circleCategory.setName(request.getName());
        return Result.bool(circleCategoryService.updateById(circleCategory));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除圈子种类")
    @RequireAdmin
    public Result<Boolean> deleteCircleCategory(@PathVariable Long id) {
        return Result.bool(circleCategoryService.removeById(id));
    }

    // endregion

    @GetMapping("/of/{categoryId}")
    @ApiOperation("获取某种类下的所有圈子")
    @Cache(expire = 1000*60*60*10L)
    public Result<List<Circle>> getCircleListByCategoryId(@PathVariable Long categoryId) {
        return Result.ok(circleCategoryService.getCircleListByCategoryId(categoryId));
    }


    private LambdaQueryWrapper<CircleCategory> getQueryWrapper(CircleCategoryQueryRequest circleCategoryQueryRequest) {
        String name = circleCategoryQueryRequest.getName();
        LambdaQueryWrapper<CircleCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null,CircleCategory::getName, name);
        return wrapper;
    }


}
