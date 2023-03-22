package cn.edu.zjut.userService.controller;

import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.FollowService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bert
 * @description 用户关注/粉丝 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("follow")
@Api(tags = "用户关注or粉丝")
public class FollowController {

    @Resource
    private FollowService followService;

    @PostMapping("/page/{uid}")
    @ApiOperation("关注用户列表")
    public Result<Page<User>> getFollowPage(@PathVariable Long uid, @RequestBody PageRequest pageRequest) {
        Page<User> page = followService.getFollowPage(pageRequest, uid);
        return Result.ok(page);
    }

    @PostMapping("/fan/page/{uid}")
    @ApiOperation("粉丝列表")
    public Result<Page<User>> getFanPage(@PathVariable Long uid, @RequestBody PageRequest pageRequest) {
        Page<User> page = followService.getFanPage(pageRequest, uid);
        return Result.ok(page);
    }

    @PutMapping("/{targetId}")
    @ApiOperation("关注or取消关注(返回-1取消关注成功 1关注成功 0异常）")
    public Result<Integer> toggleFollow(@PathVariable Long targetId) {
        return Result.ok(followService.toggleFollow(targetId));
    }

    @PostMapping("/joinFollow/{targetId}")
    @ApiOperation("与某用户的共同关注")
    public Result<List<UserSimpleVo>> getJoinFollowSimple(@PathVariable Long targetId) {
        return Result.ok(followService.getComFollow(targetId));
    }

}
