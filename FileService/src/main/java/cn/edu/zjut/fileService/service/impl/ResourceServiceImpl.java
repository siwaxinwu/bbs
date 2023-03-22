package cn.edu.zjut.fileService.service.impl;

import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.fileService.enums.ResourceStatusEnum;
import cn.edu.zjut.fileService.mapper.ResourceMapper;
import cn.edu.zjut.fileService.model.entity.Resource;
import cn.edu.zjut.fileService.service.ResourceService;
import cn.edu.zjut.fileService.utils.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bert
 * @description 针对表【resource(资源表)】的数据库操作Service实现
 * @createDate 2023-02-22 10:55:10
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource>
        implements ResourceService {

    @javax.annotation.Resource
    private FileUtil fileUtil;

    private static final String[] ALLOW_SUFFIX = {"png", "jpeg", "jpg","gif"};
    private static final String FILE_KEY_PRE = "zjutLive/";

    @Override
    public String uploadImg(MultipartFile file) {
        UserDto user = CurUserUtil.getCurUserDtoThrow();
        String fileSuffix = getFileSuffix(file);
        checkFileType(fileSuffix);
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uuid = IdUtil.simpleUUID();
        String key = FILE_KEY_PRE + user.getUserId() + "/" + getDate() + "/" + uuid + "."+fileSuffix;
        String url = fileUtil.uploadFile(inputStream, key);

        Resource resource = new Resource();
        resource.setId(uuid);
        resource.setUserId(user.getUserId());
        resource.setType("0");
        resource.setSize(String.valueOf(file.getSize()));
        resource.setUrl(url);
        int insertRow = this.baseMapper.insert(resource);
        if (insertRow <= 0) {
            throw new BusinessException(CodeEnum.FAIL);
        }
        return uuid;
    }

    @Override
    public List<String> objectIdsToImgUrlList(List<String> objectIds) {
        StringJoiner stringJoiner = new StringJoiner(",");
        objectIds.forEach(objectId -> {
            stringJoiner.add("'"+objectId+"'");
        });
        List<Resource> list = lambdaQuery()
                .inSql(Resource::getId, stringJoiner.toString())
                .last("order by field (id," + stringJoiner + ")")
                .list();
        return list.stream().map(resource -> {
            defaultImageUrl(resource);
            return resource.getUrl();
        }).collect(Collectors.toList());
    }

    @Override
    public String getImgUrlByObjectId(String objectId) {
        return getImgUrlByObjectId(objectId,true);
    }

    @Override
    public String getImgUrlByObjectId(String objectId, boolean check) {
        Resource resource = lambdaQuery().eq(Resource::getId, objectId).one();
        if (resource == null) {
            return null;
        }
        if (check) {
            defaultImageUrl(resource);
        }
        return resource.getUrl();
    }

    @Override
    public boolean changeStatus(String objectId, ResourceStatusEnum statusEnum) {
        Resource resource = new Resource();
        resource.setId(objectId);
        resource.setStatus(statusEnum.getValue());
        return this.baseMapper.updateById(resource) > 0;
    }

    private void defaultImageUrl(Resource resource) {
        // 图片等待审核
        if ("0".equals(resource.getStatus())) {
            resource.setUrl("http://cdn.xgcode.cn/zjutLive/system/To%20be%20reviewed.jpg");
        }
        // 图片审核未通过
        else if ("2".equals(resource.getStatus())) {
            resource.setUrl("http://cdn.xgcode.cn/zjutLive/system/notAllow.jpg");
        }
    }

    private void checkFileType(String suffix) {
        boolean contains = Arrays.asList(ALLOW_SUFFIX).contains(suffix);
        if (!contains) {
            throw new BusinessException(CodeEnum.FAIL, "不允许的图片类型");
        }
    }

    private String getFileSuffix(MultipartFile file) {
        String filename = file.getOriginalFilename();
        Objects.requireNonNull(filename);
        int index = filename.lastIndexOf('.');
        return filename.substring(index + 1);
    }

    private String getDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
}




