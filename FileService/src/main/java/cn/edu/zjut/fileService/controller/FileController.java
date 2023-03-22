package cn.edu.zjut.fileService.controller;

import cn.edu.zjut.common.annotation.RequireAdmin;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.fileService.service.ResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author lcg
 */
@RestController
@Api(tags = "文件模块")
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    @Resource
    private ResourceService resourceService;

    @ApiOperation("上传图片")
    @PostMapping("/uploadImg")
    public Result<Object> uploadImg(MultipartFile file){
        String url = resourceService.uploadImg(file);
        return Result.ok(url);
    }

    @ApiOperation("通过资源id查询图片url")
    @PostMapping("/img/{objectId}")
    @RequireAdmin
    public Result<Object> uploadImg(@PathVariable String objectId){
        String url = resourceService.getImgUrlByObjectId(objectId);
        return Result.ok(url);
    }


}
