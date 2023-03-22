package cn.edu.zjut.userService.service;

import cn.edu.zjut.userService.model.entity.UserTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author bert
* @description 针对表【user_tag(用户标签表)】的数据库操作Service
* @createDate 2023-01-12 11:28:50
*/
public interface UserTagService extends IService<UserTag> {

    /**
     * 获取某个用户的标签列表
     * @param uid uid
     * @return List<UserTag>
     */
    List<UserTag> getUserTagListByUid(Long uid);
}
