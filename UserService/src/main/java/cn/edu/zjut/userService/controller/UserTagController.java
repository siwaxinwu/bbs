package cn.edu.zjut.userService.controller;

import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.userService.model.dto.userTag.UserTagAddRequest;
import cn.edu.zjut.userService.model.dto.userTag.UserTagQueryRequest;
import cn.edu.zjut.userService.model.dto.userTag.UserTagUpdateRequest;
import cn.edu.zjut.userService.model.entity.UserTag;
import cn.edu.zjut.userService.service.UserTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bert
 * @description 用户标签 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("userTag")
@Api(tags = "用户标签")
public class UserTagController {

    @Resource
    private UserTagService userTagService;

    // region 增删改查

    @PostMapping("/list")
    @ApiOperation("查询列表")
    public Result<List<UserTag>> getUserTagList(@RequestBody UserTagQueryRequest userTagQueryRequest) {
        LambdaQueryWrapper<UserTag> wrapper = getQueryWrapper(userTagQueryRequest);
        return Result.ok(userTagService.list(wrapper));
    }

    @PostMapping("/list/{uid}")
    @ApiOperation("查询uid用户的tag列表")
    public Result<List<UserTag>> getUserTagListByUid(@PathVariable Long uid) {
        return Result.ok(userTagService.getUserTagListByUid(uid));
    }


    @PostMapping
    @ApiOperation("添加用户标签")
    @RequireAdmin
    public Result<Boolean> addUserTag(@RequestBody UserTagAddRequest userTagAddRequest) {
        UserTag userTag = new UserTag();
        userTag.setUserId(userTagAddRequest.getUserId());
        userTag.setName(userTagAddRequest.getTagName());
        return Result.ok(userTagService.save(userTag));
    }

    @PutMapping
    @ApiOperation("更新标签")
    @RequireAdmin
    public Result<Boolean> updateUserTag(@RequestBody UserTagUpdateRequest userTagUpdateRequest) {
        UserTag userTag = new UserTag();
        userTag.setId(userTagUpdateRequest.getTagId());
        userTag.setUserId(userTagUpdateRequest.getUserId());
        userTag.setName(userTagUpdateRequest.getTagName());
        return Result.ok(userTagService.updateById(userTag));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除标签")
    @RequireAdmin
    public Result<Boolean> deleteUserTag(@PathVariable Long id) {
        return Result.ok(userTagService.removeById(id));
    }

    // endregion

    private LambdaQueryWrapper<UserTag> getQueryWrapper(UserTagQueryRequest userTagQueryRequest) {
        String tagName = userTagQueryRequest.getTagName();
        Long userId = userTagQueryRequest.getUserId();
        if (tagName == null && userId == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "请至少输入一个查询条件");
        }
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(tagName != null, UserTag::getName, tagName);
        wrapper.eq(userId != null, UserTag::getUserId, userId);
        return wrapper;
    }


}
