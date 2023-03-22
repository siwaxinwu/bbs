package cn.edu.zjut.userService.controller;

import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.userService.model.dto.level.LevelAddRequest;
import cn.edu.zjut.userService.model.dto.level.LevelUpdateRequest;
import cn.edu.zjut.userService.model.entity.Level;
import cn.edu.zjut.userService.service.LevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bert
 * @description 用户等级 接口
 * @date 2023/1/10 22:47
 */
@RestController
@RequestMapping("level")
@Api(tags = "用户等级")
public class LevelController {

    @Resource
    private LevelService levelService;

    // region 增删改查

    @GetMapping("/list")
    @ApiOperation("查询等级列表")
    public Result<List<Level>> getLevelList() {
        return Result.ok(levelService.list());
    }


    @PostMapping
    @ApiOperation("添加等级")
    @RequireAdmin
    public Result<Boolean> addLevel(@RequestBody LevelAddRequest levelAddRequest) {
        Level level = new Level();
        level.setCount(levelAddRequest.getCount());
        level.setName(levelAddRequest.getLevelName());
        return Result.ok(levelService.save(level));
    }

    @PutMapping
    @ApiOperation("更新等级")
    @RequireAdmin
    public Result<Boolean> updateLevel(@RequestBody LevelUpdateRequest levelUpdateRequest) {
        Level level = new Level();
        level.setId(levelUpdateRequest.getId());
        level.setName(levelUpdateRequest.getLevelName());
        level.setCount(levelUpdateRequest.getCount());
        return Result.ok(levelService.updateById(level));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除等级")
    @RequireAdmin
    public Result<Boolean> deleteLevel(@PathVariable Long id) {
        return Result.ok(levelService.removeById(id));
    }

    // endregion

}
