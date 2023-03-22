package cn.edu.zjut.messageService.controller;

import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.messageService.model.dto.message.MessageAddRequest;
import cn.edu.zjut.messageService.model.dto.message.MessageQueryRequest;
import cn.edu.zjut.messageService.model.entity.Message;
import cn.edu.zjut.messageService.model.vo.ChatItemVo;
import cn.edu.zjut.messageService.model.vo.MessageVo;
import cn.edu.zjut.messageService.service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bert
 * @description 私信 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("message")
@Api(tags = "私信")
public class MessageController {

    @Resource
    private MessageService messageService;

    // region 增删改查

    @PostMapping("/list/page")
    @ApiOperation("分页查询对话聊天记录")
    public Result<Page<MessageVo>> getMessageList(@RequestBody MessageQueryRequest request) {
        LambdaQueryWrapper<Message> wrapper = getQueryWrapper(request);
        Page<Message> page = messageService.page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        List<MessageVo> voList = page.getRecords()
                .stream().map(message -> messageService.toMessageVo(message))
                .collect(Collectors.toList());
        Page<MessageVo> voPage = PageUtils.getCopyPage(page, voList);
        return Result.ok(voPage);
    }

    @PostMapping("/chatItem/list")
    @ApiOperation("用户查询聊天列表")
    public Result<List<ChatItemVo>> getChatItemList() {
        return Result.ok(messageService.getChatItemList());
    }

    @GetMapping("/readAll/{userId}")
    @ApiOperation("全部设为已读")
    public Result<Boolean> readAll(@PathVariable Long userId) {
        return Result.ok(messageService.readAll(userId));
    }

    @PostMapping
    @ApiOperation("用户添加私信")
    public Result<MessageVo> addMessage(@RequestBody MessageAddRequest request) {
        MessageVo messageVo = messageService.addMessage(request);
        return Result.ok(messageVo);
    }

    @DeleteMapping("/chatItem/{userId}")
    @ApiOperation("用户删除聊天列表")
    public Result<Boolean> deletechatItem(@PathVariable Long userId) {
        return Result.bool(messageService.deleteChatItem(userId));
    }

    // endregion




    private LambdaQueryWrapper<Message> getQueryWrapper(MessageQueryRequest request) {
        Long sendUserId = request.getSendUserId();
        Long receiveUserId = request.getReceiveUserId();
        if (sendUserId == null && receiveUserId == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        if (!userDto.getUserId().equals(receiveUserId)) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR,"不可以查询他人记录");
        }
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSendUserId, sendUserId)
                .eq(Message::getReceiveUserId, receiveUserId)
                .or()
                .eq(Message::getSendUserId, receiveUserId)
                .eq(Message::getReceiveUserId, sendUserId)
                .orderByDesc(Message::getCreatedTime);
        return wrapper;
    }


}
