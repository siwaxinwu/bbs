package cn.edu.zjut.userService.controller;

import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.userService.model.entity.CoinRecord;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.CoinRecordService;
import cn.edu.zjut.userService.utils.CurUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bert
 * @description 积分记录 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("coinRecord")
@Api(tags = "积分")
public class CoinRecordController {

    @Resource
    private CoinRecordService coinRecordService;

    @PostMapping("/page")
    @ApiOperation("积分记录列表")
    public Result<Page<CoinRecord>> getCoinRecordPage(@RequestBody PageRequest request) {
        User curUser = CurUserUtil.getCurUserThrow();
        PageUtils.checkPageParam(request);
        LambdaQueryWrapper<CoinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoinRecord::getUserId,curUser.getUserId()).orderByDesc(CoinRecord::getCreatedTime);
        Page<CoinRecord> page = coinRecordService.page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return Result.ok(page);
    }


}
