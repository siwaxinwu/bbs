package cn.edu.zjut.fileService.service;

import cn.edu.zjut.fileService.enums.ResourceStatusEnum;
import cn.edu.zjut.fileService.model.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author bert
* @description 针对表【resource(资源表)】的数据库操作Service
* @createDate 2023-02-22 10:55:10
*/
public interface ResourceService extends IService<Resource> {

    /**
     * 上传图片
     * @param multipartFile file
     * @return 资源id
     */
    String uploadImg(MultipartFile multipartFile);

    /**
     * 将图片资源id转为图片id
     * @param objectIds 资源ids
     * @return urlList
     */
    List<String> objectIdsToImgUrlList(List<String> objectIds);

    /**
     * 通过图片id获取url
     * 审核不通过的是提示信息图片
     * @param objectId id
     * @return url
     */
    String getImgUrlByObjectId(String objectId);

    /**
     * @param check 是否检查审核通过状态
     */
    String getImgUrlByObjectId(String objectId, boolean check);

    boolean changeStatus(String objectId, ResourceStatusEnum statusEnum);
}
