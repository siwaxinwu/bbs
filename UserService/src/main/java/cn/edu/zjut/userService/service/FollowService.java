package cn.edu.zjut.userService.service;

import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.userService.model.entity.Follow;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author bert
* @description 针对表【follow(用户关注表)】的数据库操作Service
* @createDate 2023-01-10 23:27:37
*/
public interface FollowService extends IService<Follow> {

    /**
     * 获取uid的粉丝列表
     * @param pageRequest pageRequest
     * @param uid uid
     * @return Page<User>
     */
    Page<User> getFanPage(PageRequest pageRequest, Long uid);

    /**
     * 获取uid的关注列表
     * @param pageRequest pageRequest
     * @param uid uid
     * @return Page<User>
     */
    Page<User> getFollowPage(PageRequest pageRequest, Long uid);

    /**
     * 获取uid的关注用户id列表
     * @param uid uid
     * @return Page<User>
     */
    List<Long> getFollowUserIds(Long uid);

    /**
     * 获取粉丝用户id列表
     * @param uid uid
     * @return Page<User>
     */
    List<Long> getFanIds(Long uid);

    /**
     * 切换对uid用户的关注
     * @param targetId 要关注or取消关注目标uid
     * @return true-切换成功 false-切换失败
     */
    int toggleFollow(Long targetId);

    /**
     * 获取与targetId用户的共同关注
     * @param targetId targetId
     * @return 共同关注列表
     */
    List<UserSimpleVo> getComFollow(Long targetId);

    /**
     * 判断userid是否关注到targetId
     * @param userId userId
     * @param targetId targetId
     * @return Boolean
     */
    Boolean isFollow(Long userId, Long targetId);
}
