package cn.edu.zjut.userService.controller;

import cn.edu.zjut.common.annotation.*;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.userService.model.dto.LoginDto;
import cn.edu.zjut.userService.model.dto.RegisterDto;
import cn.edu.zjut.userService.model.dto.user.UserAddRequest;
import cn.edu.zjut.userService.model.dto.user.UserQueryRequest;
import cn.edu.zjut.userService.model.dto.user.UserUpdateRequest;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.model.vo.UserBasicInfoVo;
import cn.edu.zjut.userService.service.UserService;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.userService.utils.CurUserUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description 用户基础管理
 * @Author bert
 * @Date 2023/1/9 16:57
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户管理")
public class UserController {

    @Resource
    private UserService userService;

    // region 管理员权限

    @PostMapping("/list/page")
    @ApiOperation("分页获取用户")
    @RequireAdmin
    public Result<Page<User>> getUser(@RequestBody UserQueryRequest userQueryRequest) {
        PageUtils.checkPageParam(userQueryRequest);
        Page<User> page = userService.page(userQueryRequest);
        return Result.ok(page);
    }

    @PostMapping
    @ApiOperation("添加用户")
    @RequireAdmin
    public Result<Boolean> addUser(@RequestBody UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        return Result.ok(userService.save(user));
    }

    @DeleteMapping("/{uid}")
    @ApiOperation("删除用户")
    @RequireAdmin
    public void deleteUser(@PathVariable Long uid) {
        userService.removeById(uid);
    }

    // endregion

    @PutMapping
    @ApiOperation("更新当前用户")
    @Log
    public Result<Boolean> updateCurUser(@RequestBody UserUpdateRequest request) {
        User curUser = CurUserUtil.getCurUserThrow();
        request.setUserId(curUser.getUserId());
        return Result.ok(userService.update(request));
    }


    @PostMapping("/login")
    @ApiOperation("登录")
    @Log
    public Result<UserSimpleVo> login(@RequestBody LoginDto loginDto) {
        return Result.ok(userService.login(loginDto));
    }

    @PostMapping("/register")
    @ApiOperation("注册")
    public Result<UserSimpleVo> register(@RequestBody @Validated RegisterDto registerDto) {
        return Result.ok(userService.register(registerDto));
    }

    @PostMapping("/sign")
    @ApiOperation("签到")
    public Result<Boolean> register() {
        return Result.ok(userService.sign());
    }

    @GetMapping("/currentUserInfo")
    @ApiOperation("当前用户信息")
    public Result<UserBasicInfoVo> currentUserInfo() {
        return Result.ok(userService.currentLoginUserVo());
    }

    @GetMapping("/info/{uid}")
    @ApiOperation("某用户信息")
    @Cache
    public Result<UserBasicInfoVo> getUserInfoByUid(@PathVariable Long uid) {
        if (uid <= 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        return Result.ok(userService.getUserBasicInfoVo(uid));
    }

    @PostMapping("/active/other")
    @ApiOperation("激活他人账号")
    @Log
    public Result<Boolean> activeOther(String studentNo) {
        return Result.ok(userService.activeOther(studentNo));
    }

    @PostMapping("/active/by/coin")
    @ApiOperation("积分激活账号")
    @Log
    public Result<Boolean> activeByCoin() {
        return Result.ok(userService.activeByCoin());
    }



}
