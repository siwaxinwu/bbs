package cn.edu.zjut.circleAndTopicService.service.impl;

import cn.edu.zjut.circleAndTopicService.mapper.CircleJoinMapper;
import cn.edu.zjut.circleAndTopicService.mapper.CircleMapper;
import cn.edu.zjut.circleAndTopicService.model.entity.Circle;
import cn.edu.zjut.circleAndTopicService.model.entity.CircleJoin;
import cn.edu.zjut.circleAndTopicService.model.vo.CircleVo;
import cn.edu.zjut.circleAndTopicService.service.CircleService;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.common.redis.RedisUtils;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author bert
* @description 针对表【circle(圈子表)】的数据库操作Service实现
* @createDate 2023-01-12 21:36:36
*/
@Service
public class CircleServiceImpl extends ServiceImpl<CircleMapper, Circle>
    implements CircleService {

    @Resource
    private CircleJoinMapper circleJoinMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;

    @Override
    public boolean save(Circle circle) {
        UserDto curUser = CurUserUtil.getCurUserDtoThrow();
        circle.setId(null);
        circle.setCreatorId(curUser.getUserId());
        this.baseMapper.insert(circle);
        // 创建成功后自动加入圈子
        this.joinCircle(circle.getId());
        return true;
    }

    @Override
    public CircleVo getVoById(Long circleId) {
        Circle circle = this.baseMapper.selectById(circleId);
        if (circle == null) {
            throw new BusinessException(CodeEnum.FAIL, "该圈子不存在");
        }
        CircleVo circleVo = new CircleVo();
        BeanUtils.copyProperties(circle, circleVo);
        setIsFollow(circleVo, circleId);
        setJoinedUserList(circleVo,circleId,3);
        return circleVo;
    }

    private void setJoinedUserList(CircleVo circleVo, Long circleId, Integer count) {
        List<Long> joinUserIds = getJoinUserIds(circleId);
        List<UserSimpleVo> collect = joinUserIds.stream()
                                                .limit(count)
                                                .map(userService::getUserSimpleVo)
                                                .collect(Collectors.toList());
        circleVo.setJoinUserList(collect);
    }

    /**
     * 设置vo对象的isFollow属性
     */
    private void setIsFollow(CircleVo circleVo, Long circleId) {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return;
        }
        boolean isJoin = hasJoined(circleId, userDto.getUserId());
        circleVo.setIsFollow(isJoin);
    }

    @Override
    public List<Circle> getJoinList(Long uid) {
        List<Long> joinCircleIds = getJoinCircleIds(uid);
        return joinCircleIds.stream().map(this::getById).collect(Collectors.toList());
    }


    @Override
    public List<Circle> getCreateList(Long uid) {
        if (uid == null || uid < 0) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Circle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Circle::getCreatorId, uid);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public List<Long> getJoinUserIds(Long circleId) {
        // 圈子粉丝key
        String circleFansKey = RedisConstants.getCircleFansKey(circleId);
        Boolean hasKey = stringRedisTemplate.hasKey(circleFansKey);
        // 没有缓存记录，更新缓存
        if (hasKey==null || !hasKey) {
            LambdaQueryWrapper<CircleJoin> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CircleJoin::getCircleId, circleId);
            List<CircleJoin> circleJoins = circleJoinMapper.selectList(wrapper);
            // 没有用户加入圈子
            if (circleJoins.size() == 0) {
                return Collections.emptyList();
            }
            // 有用户加入圈子，更新缓存
            String[] strings = RedisUtils
                    .listToStringArray(circleJoins, circleJoin -> String.valueOf(circleJoin.getUserId()));
            stringRedisTemplate.opsForSet().add(circleFansKey, strings);
        }
        // 将string类型的用户id转换为long类型
        List<Long> collect = stringRedisTemplate.opsForSet()
                .members(circleFansKey)
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public Boolean joinCircle(Long id) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        if (!isCircleExist(id)) {
            throw new BusinessException(CodeEnum.FAIL, "该圈子不存在或已被删除");
        }
        Long userId = userDto.getUserId();
        if (hasJoined(id, userId)) {
            return true;
        }
        CircleJoin circleJoin = new CircleJoin();
        circleJoin.setCircleId(id);
        circleJoin.setUserId(userId);
        boolean isSuccess = circleJoinMapper.insert(circleJoin) > 0;
        if (isSuccess) {
            // 添加用户关注圈子缓存
            String followSetKey = RedisConstants.getFollowCircleKey(userId);
            stringRedisTemplate.opsForSet().add(followSetKey, id.toString());
            // 添加圈子粉丝列表
            String circleFansKey = RedisConstants.getCircleFansKey(id);
            stringRedisTemplate.opsForSet().add(circleFansKey, userId.toString());
            // 圈子人数+1
            lambdaUpdate().setSql("join_count=join_count+1").eq(Circle::getId,id).update();
        }
        return isSuccess;
    }

    @Override
    public Boolean leaveCircle(Long id) {
        UserDto userDto = CurUserUtil.getCurUserDtoThrow();
        Long userId = userDto.getUserId();
        if (!hasJoined(id, userId)) {
            return true;
        }
        Circle circle = this.getById(id);
        if (circle.getCreatorId().equals(userId)) {
            throw new BusinessException(CodeEnum.OPERATION_ERROR, "不可以退出自己创建的圈子!");
        }
        LambdaQueryWrapper<CircleJoin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleJoin::getCircleId, id);
        wrapper.eq(CircleJoin::getUserId , userId);
        boolean isSuccess = circleJoinMapper.delete(wrapper) > 0;
        if (isSuccess) {
            // 移出用户关注圈子
            String followSetKey = RedisConstants.getFollowCircleKey(userId);
            stringRedisTemplate.opsForSet().remove(followSetKey, id.toString());
            // 移出圈子粉丝列表
            String circleFansKey = RedisConstants.getCircleFansKey(id);
            stringRedisTemplate.opsForSet().remove(circleFansKey, userId.toString());
            // 删除圈子的未读数key
            String unReadCircleCountKey = RedisConstants.getUnReadCircleCountKey(userId, id);
            stringRedisTemplate.delete(unReadCircleCountKey);
            // 圈子人数-1
            lambdaUpdate().setSql("join_count=join_count-1").eq(Circle::getId,id).update();
        }
        return isSuccess;
    }


    // 判断用户是否加入圈子
    private boolean hasJoined(Long circleId, Long uid) {
        if (circleId == null || uid == null) {
            return false;
        }
        List<Long> joinCircleIds = getJoinCircleIds(uid);
        return joinCircleIds.stream().anyMatch(circleId::equals);
    }

    // 判断圈子是否存在
    private boolean isCircleExist(Long circleId) {
        if (circleId == null) {
            return false;
        }
        LambdaQueryWrapper<Circle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Circle::getId, circleId);
        return this.baseMapper.selectOne(wrapper) != null;
    }

    /**
     * 刷新该用户关注圈子缓存
     */
    private void updateFollowCircleSet(Long uid) {
        String followSetKey = RedisConstants.getFollowCircleKey(uid);
        stringRedisTemplate.delete(followSetKey);
        LambdaQueryWrapper<CircleJoin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleJoin::getUserId, uid);
        List<CircleJoin> circleJoins = circleJoinMapper.selectList(wrapper);
        // 该用户未加入任何圈子
        if (circleJoins.size() == 0) {
            return;
        }
        // 将加入的圈子id放入缓存
        String[] strings = RedisUtils
                .listToStringArray(circleJoins, circleJoin -> String.valueOf(circleJoin.getCircleId()));
        stringRedisTemplate.opsForSet().add(followSetKey,strings);
    }

    // 获取加入的圈子id列表
    private List<Long> getJoinCircleIds(Long uid) {
        if (uid == null || uid < 0) {
            return Collections.emptyList();
        }
        String followSetKey = RedisConstants.getFollowCircleKey(uid);
        Boolean hasKey = stringRedisTemplate.hasKey(followSetKey);
        // 若key不存在，更新缓存
        if (hasKey==null || !hasKey) {
            this.updateFollowCircleSet(uid);
        }
        // 获取所有id，转换成long类型并返回
        Set<String> range = stringRedisTemplate.opsForSet().members(followSetKey);
        assert range != null;
        return range.stream().map(Long::valueOf).collect(Collectors.toList());
    }


}




