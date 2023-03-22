package cn.edu.zjut.circleAndTopicService.controller;

import cn.edu.zjut.circleAndTopicService.model.vo.CircleVo;
import cn.edu.zjut.common.annotation.Cache;
import cn.edu.zjut.common.annotation.Log;
import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.circleAndTopicService.model.dto.circle.CircleAddOrUpdateRequest;
import cn.edu.zjut.circleAndTopicService.model.dto.circle.CircleQueryRequest;
import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import cn.edu.zjut.circleAndTopicService.service.CircleService;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.utils.CurUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author bert
 * @description 圈子 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("circle")
@Api(tags = "圈子")
public class CircleController {

    @Resource
    private CircleService circleService;

    // region 增删改查

    @GetMapping("/{circleId}")
    @ApiOperation("查询某个圈子详细")
    public Result<CircleVo> getCircleById(@PathVariable Long circleId) {
        return Result.ok(circleService.getVoById(circleId));
    }

    @PostMapping("/list")
    @ApiOperation("查询")
    public Result<List<Circle>> getCircleList(@RequestBody CircleQueryRequest circleQueryRequest) {
        LambdaQueryWrapper<Circle> wrapper = getQueryWrapper(circleQueryRequest);
        return Result.ok(circleService.list(wrapper));
    }


    @PostMapping
    @ApiOperation("添加圈子")
    @RequireAdmin
    public Result<Boolean> addCircle(@RequestBody CircleAddOrUpdateRequest request) {
        Circle circle = new Circle();
        circle.setCreatorId(request.getCreatorId());
        circle.setName(request.getName());
        circle.setAvatar(request.getAvatar());
        circle.setDescription(request.getDescription());
        circle.setCategoryId(request.getCategoryId());
        circle.setId(null);
        return Result.ok(circleService.save(circle));
    }

    @PutMapping
    @ApiOperation("更新圈子")
    @RequireAdmin
    public Result<Boolean> updateCircle(@RequestBody CircleAddOrUpdateRequest request) {
        Circle circle = new Circle();
        BeanUtils.copyProperties(request,circle);
        circle.setId(null);
        LambdaUpdateWrapper<Circle> wrapper = new LambdaUpdateWrapper<>();
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        wrapper.eq(Circle::getCreatorId, userDto.getUserId());
        wrapper.eq(Circle::getId, circle.getId());
        return Result.bool(circleService.update(circle,wrapper));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除圈子")
    @RequireAdmin
    public Result<Boolean> deleteCircle(@PathVariable Long id) {
        return Result.bool(circleService.removeById(id));
    }

    // endregion 增删改查

    // region 加入圈子记录

    @PostMapping("/joinList/{uid}")
    @ApiOperation("查询uid的加入的圈子列表")
    public Result<List<Circle>> getJoinCircleListByUid(@PathVariable Long uid) {
        return Result.ok(circleService.getJoinList(uid));
    }

    @PostMapping("/createList/{uid}")
    @ApiOperation("查询uid创建的圈子列表")
    public Result<List<Circle>> getCreateCircleListByUid(@PathVariable Long uid) {
        return Result.ok(circleService.getCreateList(uid));
    }

    @PostMapping("/join/{id}")
    @ApiOperation("加入圈子")
    @Log
    public Result<Boolean> joinCircle(@PathVariable Long id) {
        return Result.bool(circleService.joinCircle(id));
    }

    @PostMapping("/leave/{id}")
    @ApiOperation("退出圈子")
    @Log
    public Result<Boolean> leaveCircle(@PathVariable Long id) {
        return Result.bool(circleService.leaveCircle(id));
    }

    // endregion 加入圈子记录

    // region 数据


    // endregion

    private LambdaQueryWrapper<Circle> getQueryWrapper(CircleQueryRequest circleQueryRequest) {
        String name = circleQueryRequest.getName();
        Long creatorId = circleQueryRequest.getCreatorId();
        if (name == null && creatorId == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "请至少输入一个查询条件");
        }
        LambdaQueryWrapper<Circle> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Circle::getName, name);
        wrapper.eq(creatorId != null, Circle::getCreatorId, creatorId);
        return wrapper;
    }


}
