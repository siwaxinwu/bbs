package cn.edu.zjut.messageService.controller;

import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.messageService.enums.NotificationTypeEnum;
import cn.edu.zjut.messageService.model.dto.notification.NotificationAddRequest;
import cn.edu.zjut.messageService.model.dto.notification.NotificationQueryRequest;
import cn.edu.zjut.messageService.model.entity.Notification;
import cn.edu.zjut.messageService.model.vo.NotificationItemVo;
import cn.edu.zjut.messageService.model.vo.NotificationVo;
import cn.edu.zjut.messageService.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author bert
 * @description 通知 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("notification")
@Api(tags = "通知")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    // region 增删改查

    @PostMapping("/list/page")
    @ApiOperation("分页查询通知")
    public Result<Page<NotificationVo>> getNotificationPage(@RequestBody NotificationQueryRequest request) {
        LambdaQueryWrapper<Notification> wrapper = getQueryWrapper(request);
        Page<NotificationVo> voPage = notificationService.voPage(request.getPageNum(), request.getPageSize(), wrapper);
        return Result.ok(voPage);
    }

    @PostMapping("/interact/list/page")
    @ApiOperation("分页查询互动通知")
    public Result<Page<NotificationVo>> getInteractNotification(@RequestBody PageRequest request) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiveUserId,userDto.getUserId())
                .ne(Notification::getType, NotificationTypeEnum.SYSTEM.getValue())
                .orderByDesc(Notification::getCreatedTime);
        Page<NotificationVo> voPage = notificationService.voPage(request.getPageNum(), request.getPageSize(), wrapper);
        return Result.ok(voPage);
    }

    @PostMapping("/systemNotificationItem")
    @ApiOperation("未读系统通知")
    public Result<NotificationItemVo> systemNotificationItem() {
        return Result.ok(notificationService.systemNotificationItem());
    }
    @PostMapping("/interactNotificationItem")
    @ApiOperation("未读互动通知")
    public Result<NotificationItemVo> interactNotificationItem() {
        return Result.ok(notificationService.interactNotificationItem());
    }



    @PostMapping
    @ApiOperation("添加通知")
    @RequireAdmin
    public Result<NotificationVo> addNotification(@RequestBody NotificationAddRequest request) {
        return Result.bool(notificationService.add(request));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除通知")
    @RequireAdmin
    public Result<Boolean> deleteNotification(@PathVariable Long id) {
        return Result.bool(notificationService.removeById(id));
    }

    // endregion


    //region 数据

    @GetMapping("/readAll/system")
    @ApiOperation("清空系统通知未读数")
    public Result<Boolean> readAllOfSystem() {
        return Result.ok(notificationService.readAllOfSystem());
    }

    @GetMapping("/readAll/interact")
    @ApiOperation("清空互动通知未读数")
    public Result<Boolean> readAllOfInteract() {
        return Result.ok(notificationService.readAllOfInteract());
    }

    //endregion end 数据

    private LambdaQueryWrapper<Notification> getQueryWrapper(NotificationQueryRequest request) {
        String type = request.getType();
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Notification::getType, type);
        wrapper.eq( Notification::getReceiveUserId, userDto.getUserId());
        return wrapper;
    }


}
