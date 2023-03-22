package cn.edu.zjut.userService.controller;

import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.userService.model.entity.Block;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.service.BlockService;
import cn.edu.zjut.userService.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author bert
 * @description 拉黑 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("block")
@Api(tags = "拉黑")
public class BlockController {

    @Resource
    private BlockService blockService;
    @Resource
    private UserService userService;

    // region 增删改查

    @PostMapping("/isBlocked/{targetUserId}")
    @ApiOperation("判断用户是否被拉黑")
    public Result<Boolean> isBlockEd(@PathVariable Long targetUserId) {
        check(targetUserId);
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        boolean blocked = blockService.isBlocked(userDto.getUserId(), targetUserId);
        return Result.ok(blocked);
    }

    @PutMapping("/{targetUserId}")
    @ApiOperation("拉黑用户")
    public Result<Boolean> addBlock(@PathVariable Long targetUserId) {
        check(targetUserId);
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        boolean blocked = blockService.isBlocked(userDto.getUserId(), targetUserId);
        if (blocked) {
            throw new BusinessException(CodeEnum.FAIL,"已经拉黑过了");
        }
        Block block = new Block();
        block.setUserId(userDto.getUserId());
        block.setTargetUserId(targetUserId);
        return Result.ok(blockService.save(block));
    }


    @DeleteMapping("/{targetUserId}")
    @ApiOperation("取消拉黑用户")
    public Result<Boolean> deleteBlock(@PathVariable Long targetUserId) {
        check(targetUserId);
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        LambdaQueryWrapper<Block> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Block::getUserId,userDto.getUserId())
                        .eq(Block::getTargetUserId,targetUserId);
        return Result.ok(blockService.remove(wrapper));
    }

    // endregion

    private void check(Long targetUserId) {
        if (targetUserId == null || targetUserId < 0) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR,"该用户不存在");
        }
        User one = userService.getById(targetUserId);
        if (one == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR,"该用户不存在");
        }
    }
}
