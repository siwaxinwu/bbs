package cn.edu.zjut.userService.service.impl;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.PageRequest;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.redis.RedisUtils;
import cn.edu.zjut.userService.model.entity.Follow;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.UserService;
import cn.edu.zjut.userService.utils.CurUserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.userService.service.FollowService;
import cn.edu.zjut.userService.mapper.FollowMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bert
 * @description 针对表【follow(用户关注表)】的数据库操作Service实现
 * @createDate 2023-01-10 23:27:37
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
        implements FollowService {

    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Page<User> getFanPage(PageRequest pageRequest, Long uid) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        return getFollowOrFanPage(new Page<>(pageNum, pageSize), uid, true);
    }

    @Override
    public Page<User> getFollowPage(PageRequest pageRequest, Long uid) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        return getFollowOrFanPage(new Page<>(pageNum, pageSize), uid, false);
    }

    @Override
    public List<Long> getFollowUserIds(Long uid) {
        String followUserKey = RedisConstants.getFollowUserKey(uid);
        Boolean hasKey = stringRedisTemplate.hasKey(followUserKey);
        // 数据未缓存，进行更新缓存
        if (hasKey==null || !hasKey) {
            List<Follow> list = lambdaQuery().eq(Follow::getUserId, uid).list();
            if (list==null || list.size()==0) {
                return Collections.emptyList();
            }
            String[] strings = RedisUtils.listToStringArray(list, follow -> String.valueOf(follow.getFollowUserId()));
            stringRedisTemplate.opsForSet().add(followUserKey, strings);
        }
        Set<String> members = stringRedisTemplate.opsForSet().members(followUserKey);
        return members.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    @Override
    public List<Long> getFanIds(Long uid) {
        String fanKey = RedisConstants.getFanKey(uid);
        Boolean hasKey = stringRedisTemplate.hasKey(fanKey);
        // 数据未缓存，进行更新缓存
        if (hasKey==null || !hasKey) {
            // 关注了uid的用户列表
            List<Follow> list = lambdaQuery().eq(Follow::getFollowUserId, uid).list();
            if (list==null || list.size()==0) {
                return Collections.emptyList();
            }
            String[] strings = RedisUtils.listToStringArray(list, follow -> String.valueOf(follow.getUserId()));
            stringRedisTemplate.opsForSet().add(fanKey, strings);
        }
        Set<String> members = stringRedisTemplate.opsForSet().members(fanKey);
        return members.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int toggleFollow(Long targetId) {
        User curUser = CurUserUtil.getCurUser();
        if (curUser == null) {
            throw new BusinessException(CodeEnum.NOT_LOGIN);
        }
        if (curUser.getUserId().equals(targetId)) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR, "不能关注自己");
        }
        Long userId = curUser.getUserId();
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, userId);
        wrapper.eq(Follow::getFollowUserId, targetId);
        Follow follow = this.baseMapper.selectOne(wrapper);

        String followSetKey = RedisConstants.getFollowUserKey(curUser.getUserId());
        // -1取消关注成功 1关注成功 0异常
        int resultFlag = 0;
        // 取消关注
        if (follow != null) {
            if (this.baseMapper.delete(wrapper) > 0) {
                stringRedisTemplate.opsForSet().remove(followSetKey, String.valueOf(targetId));
                resultFlag = -1;
                // 用户粉丝数-1
                userService.lambdaUpdate().setSql("fan_count=fan_count-1").eq(User::getUserId,targetId).update();
                // 关注数-1
                userService.lambdaUpdate().setSql("follow_count=follow_count-1").eq(User::getUserId,curUser.getUserId()).update();
            }
        } else {
            // 关注用户
            Follow insertFollow = new Follow();
            insertFollow.setUserId(userId);
            insertFollow.setFollowUserId(targetId);
            if (this.baseMapper.insert(insertFollow) > 0) {
                stringRedisTemplate.opsForSet().add(followSetKey, String.valueOf(targetId));
                resultFlag = 1;
                // 用户粉丝数+1
                userService.lambdaUpdate().setSql("fan_count=fan_count+1").eq(User::getUserId,targetId).update();
                // 关注数+1
                userService.lambdaUpdate().setSql("follow_count=follow_count+1").eq(User::getUserId,curUser.getUserId()).update();
            }
        }
        userService.updateCacheUser(userService.getById(curUser.getUserId()));
        return resultFlag;
    }

    @Override
    public List<UserSimpleVo> getComFollow(Long targetId) {
        User httpCurUser = CurUserUtil.getCurUser();
        if (httpCurUser == null || targetId < 0 || httpCurUser.getUserId().equals(targetId)) {
            return Collections.emptyList();
        }
        String followSetKey = RedisConstants.getFollowUserKey(httpCurUser.getUserId());
        String targetFollowSetKey = RedisConstants.getFollowUserKey(targetId);
        // 查找本人关注缓存
        Set<String> followIds = stringRedisTemplate.opsForSet().members(followSetKey);
        if (followIds == null || followIds.size()==0) {
            updateFollowCacheSet(httpCurUser.getUserId());
        }
        // 查找对方关注缓存
        Set<String> targetFollowIds = stringRedisTemplate.opsForSet().members(targetFollowSetKey);
        if (targetFollowIds == null || targetFollowIds.size()==0) {
            updateFollowCacheSet(targetId);
        }
        // 获取共同关注
        Set<String> joinFollowIds = stringRedisTemplate.opsForSet().intersect(followSetKey, targetFollowSetKey);
        if (joinFollowIds == null) {
            return Collections.emptyList();
        }
        return joinFollowIds.stream().map(idStr -> userService.getUserSimpleVo(Long.valueOf(idStr)))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean isFollow(Long userId, Long targetId) {
        String followSetKey = RedisConstants.getFollowUserKey(userId);
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(followSetKey))) {
            updateFollowCacheSet(userId);
        }
        return stringRedisTemplate.opsForSet().isMember(followSetKey, targetId.toString());
    }

    // region 私有方法

    /**
     * 刷新该用户所有关注缓存
     */
    private void updateFollowCacheSet(Long uid) {
        String followSetKey = RedisConstants.getFollowUserKey(uid);
        stringRedisTemplate.delete(followSetKey);
        List<User> followList = getFollowList(uid);
        // 未关注用户
        if (followList.size() == 0) {
            return;
        }
        // 更新
        String[] strings = RedisUtils
                .listToStringArray(followList, user -> String.valueOf(user.getUserId()));
        stringRedisTemplate.opsForSet().add(followSetKey,strings);
    }

    /**
     * 获取uid的所有关注用户
     */
    private List<User> getFollowList(Long uid) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getUserId, uid);
        List<Follow> follows = this.baseMapper.selectList(wrapper);
        ArrayList<User> userList = new ArrayList<>(follows.size());
        for (Follow follow : follows) {
            userList.add(userService.getById(follow.getFollowUserId()));
        }
        return userList;
    }

    /**
     * 获取关注或粉丝
     */
    private Page<User> getFollowOrFanPage(Page<Follow> page, Long uid, Boolean getFan) {
        if (page == null || uid == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        // 获取粉丝列表
        if (getFan == null || getFan) {
            wrapper.eq(Follow::getFollowUserId, uid);
        } else {
            // 获取关注列表
            wrapper.eq(Follow::getUserId, uid);
        }
        Page<Follow> followPage = this.baseMapper.selectPage(page, wrapper);
        List<Follow> records = followPage.getRecords();
        // 将page中的id转换成user对象
        ArrayList<User> userList = new ArrayList<>(records.size());
        for (Follow follow : records) {
            if (getFan == null || getFan) {
                userList.add(userService.getById(follow.getUserId()));
            } else {
                userList.add(userService.getById(follow.getFollowUserId()));
            }
        }
        // 封装userPage
        Page<User> userPage = new Page<>();
        userPage.setCurrent(followPage.getCurrent());
        userPage.setSize(followPage.getSize());
        userPage.setTotal(followPage.getTotal());
        userPage.setRecords(userList);
        return userPage;
    }


    // endregion 私有方法
}




