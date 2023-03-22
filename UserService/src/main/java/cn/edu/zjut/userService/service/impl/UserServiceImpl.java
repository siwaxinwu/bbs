package cn.edu.zjut.userService.service.impl;

import cn.edu.zjut.common.constants.FileConstants;
import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.enums.LoginTypeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.ChaoXingUser;
import cn.edu.zjut.common.model.WeJhUser;
import cn.edu.zjut.common.redis.RedisConstants;
import cn.edu.zjut.fileService.service.ResourceService;
import cn.edu.zjut.userService.enums.CoinOperationTypeEnum;
import cn.edu.zjut.userService.enums.CoinRecordNoteEnum;
import cn.edu.zjut.userService.enums.SexEnum;
import cn.edu.zjut.userService.enums.UserStatusEnum;
import cn.edu.zjut.userService.mapper.UserMapper;
import cn.edu.zjut.userService.model.dto.LoginDto;
import cn.edu.zjut.userService.model.dto.RegisterDto;
import cn.edu.zjut.userService.model.dto.user.UserQueryRequest;
import cn.edu.zjut.userService.model.dto.user.UserUpdateRequest;
import cn.edu.zjut.userService.model.entity.CoinRecord;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.model.vo.UserBasicInfoVo;
import cn.edu.zjut.userService.service.*;
import cn.edu.zjut.userService.utils.ChaoXingUtil;
import cn.edu.zjut.common.utils.HttpUtils;
import cn.edu.zjut.common.utils.PageUtils;
import cn.edu.zjut.userService.utils.CurUserUtil;
import cn.edu.zjut.userService.utils.WeJhUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.Objects;

/**
 * @Description 用户管理 服务类
 * @Author bert
 * @Date 2023/1/9 17:09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private LevelService levelService;
    @Resource
    private FollowService followService;
    @Resource
    private UserTagService userTagService;
    @Resource
    private CoinRecordService coinRecordService;
    @Resource
    private ResourceService resourceService;

    @Override
    public Page<User> page(UserQueryRequest userQueryRequest) {
        PageUtils.checkPageParam(userQueryRequest);
        return page(new Page<>(userQueryRequest.getPageNum(), userQueryRequest.getPageSize()),
                getQueryWrapper(userQueryRequest));
    }

    @Override
    public UserSimpleVo login(LoginDto loginDto) {
        validateCaptcha(loginDto.getCaptcha(), loginDto.getCaptchaId(), true);
        User user;
        // 账号密码登录
        if (loginDto.getLoginType() == LoginTypeEnum.PASSWORD.getCode()) {
            user = passwdLogin(loginDto.getUserName(), loginDto.getPassword());
        }
        // 超星登录
        else if (loginDto.getLoginType() == LoginTypeEnum.CHAO_XING.getCode()) {
            user = chaoXingLogin(loginDto.getUserName(), loginDto.getPassword());
        }
        // 微精弘登录
        else if (loginDto.getLoginType() == LoginTypeEnum.WE_JH.getCode()) {
            user = weJhLogin(loginDto.getUserName(), loginDto.getPassword());
        }
        else {
            throw new BusinessException(CodeEnum.FAIL, "非法登录请求");
        }
        String token = cacheUser(user);
        UserSimpleVo userSimpleVo = toUserSimpleVo(user);
        userSimpleVo.setToken(token);
        updateLoginDateAndIp(user.getUserId());
        return userSimpleVo;
    }

    @Override
    public UserSimpleVo register(RegisterDto registerDto) {
        throw new BusinessException(CodeEnum.NOT_ALLOW,"系统未开放此功能");
//        if (registerDto.getCaptchaId() == null) {
//            throw new BusinessException(CodeEnum.PARAMS_ERROR);
//        }
//        // 校验验证码
//        validateCaptcha(registerDto.getCaptcha(), registerDto.getCaptchaId(), true);
//        // 保存新用户
//        User user = new User();
//        user.setNickName(registerDto.getNickName());
//        user.setUserName(registerDto.getUserName());
//        user.setPassword(registerDto.getPassword());
//        user.setAvatar(randomAvatar());
//        this.baseMapper.insert(user);
//        return toUserSimpleVo(user);
    }

    @Override
    public UserBasicInfoVo currentLoginUserVo() {
        User curUser = CurUserUtil.getCurUserThrow();
        return toUserBasicInfoVo(curUser);
    }

    @Override
    public UserBasicInfoVo getUserBasicInfoVo(long uid) {
        User user = this.baseMapper.selectById(uid);
        if (user == null) {
            throw new BusinessException(CodeEnum.FAIL,"用户不存在");
        }
        User curUser = CurUserUtil.getCurUser();
        UserBasicInfoVo userBasicInfoVo = toUserBasicInfoVo(user);
        // 判断当前用户是否关注uid
        if (curUser != null && uid != curUser.getUserId()) {
            userBasicInfoVo.setIsFollow(followService.isFollow(curUser.getUserId(),uid));
        }
        return userBasicInfoVo;
    }
    @Override
    public UserBasicInfoVo toUserBasicInfoVo(User user) {
        UserBasicInfoVo userBasicInfoVo = new UserBasicInfoVo();
        BeanUtils.copyProperties(user, userBasicInfoVo);
        String levelName = levelService.getLevelNameByCount(user.getLevelCount());
        userBasicInfoVo.setLevelName(levelName);
        // 获取用户tagList
        userBasicInfoVo.setTags(userTagService.getUserTagListByUid(user.getUserId()));
        return userBasicInfoVo;
    }

    @Override
    public UserSimpleVo getUserSimpleVo(Long uid) {
        return toUserSimpleVo(this.baseMapper.selectById(uid));
    }

    @Override
    public UserSimpleVo toUserSimpleVo(User user) {
        UserSimpleVo userSimpleVo = new UserSimpleVo();
        userSimpleVo.setUserId(user.getUserId());
        userSimpleVo.setNickName(user.getNickName());
        userSimpleVo.setUserType(user.getUserType());
        userSimpleVo.setGender(user.getGender());
        userSimpleVo.setAvatar(user.getAvatar());
        userSimpleVo.setStatus(user.getStatus());
        return userSimpleVo;
    }

    @Override
    public boolean update(UserUpdateRequest request) {
        String gender = request.getGender();
        if (gender!=null) {
            SexEnum.transform(gender);
        }
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        if (request.getGrade() != null && request.getGrade() > year+1) {
            throw new BusinessException(CodeEnum.FAIL,"年级选择错误");
        }
        if (request.getBirthday()!=null && new Date().before(request.getBirthday())) {
            throw new BusinessException(CodeEnum.FAIL,"生日选择错误");
        }
        User user = new User();
        BeanUtils.copyProperties(request,user);
        Long userId = request.getUserId();
        user.setUserId(userId);
        // 头像信息
        String avatarId = request.getAvatarId();
        if (avatarId!=null) {
            String url = resourceService.getImgUrlByObjectId(avatarId, false);
            user.setAvatar(url);
        }
        boolean isSuccess = updateById(user);
        updateCacheUser(userId);
        return isSuccess;
    }

    @Override
    public Boolean sign() {
        User user = CurUserUtil.getCurUserThrow();
        LocalDateTime now = LocalDateTime.now();
        String key = RedisConstants.getSignKey(user.getUserId(),now);
        int dayOfMonth = now.getDayOfMonth();
        Boolean rowFlag = stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        if (Boolean.TRUE.equals(rowFlag)) {
            throw new BusinessException(CodeEnum.FAIL,"您已签到过了");
        }
        if (dayOfMonth == 1) {
            // 第一天，重置连续签到数
            updateSignCount(user.getUserId(),1);
        } else {
            // 非本月第一天，判断昨天是否签到，若签到则增加签到数，否则重置签到数
            Boolean bit = stringRedisTemplate.opsForValue().getBit(key, dayOfMonth - 2);
            if (Boolean.TRUE.equals(bit)) {
                updateSignCount(user.getUserId(),user.getSignCount()+1);
            } else {
                updateSignCount(user.getUserId(),1);
            }
        }
        int addCount = 5;
        Integer newCount = user.getCoinCount() + addCount;
        updateCoinCount(user.getUserId(), newCount);
        // 更新缓存
        updateCacheUser(user.getUserId());
        addLevelCount(user.getUserId(),20);
        CoinRecord coinRecord = new CoinRecord();
        coinRecord.setUserId(user.getUserId());
        coinRecord.setCount(addCount);
        coinRecord.setOperationType(CoinOperationTypeEnum.ADD.getValue());
        coinRecord.setRemain(newCount);
        coinRecord.setNote(CoinRecordNoteEnum.SIGN.getValue());
        coinRecordService.save(coinRecord);
        return true;
    }

    @Override
    public void updateCacheUser(User user) {
        String token = HttpUtils.getHeader(RedisConstants.TOKEN);
        String key = String.format("%s:%s", RedisConstants.CACHE_USER_KEY, token);
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(user), RedisConstants.cacheUserDuration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activeOther(String studentNo) {
        // 验证输入信息
        User curUser = CurUserUtil.getCurUserThrow();
        if (curUser.getUserName().equals(studentNo)) {
            throw new BusinessException(CodeEnum.FAIL,"不可以激活本人账号");
        }
        if (curUser.getStatus().equals(UserStatusEnum.NORMAL.getValue())) {
            throw new BusinessException(CodeEnum.FAIL,"您的账号已激活，无权操作");
        }
        User byUserName = getByUserName(studentNo);
        if (byUserName == null) {
            throw new BusinessException(CodeEnum.FAIL,"该账号未登录过系统");
        }
        if (byUserName.getStatus().equals(UserStatusEnum.NORMAL.getValue())) {
            throw new BusinessException(CodeEnum.FAIL,"该账号已激活");
        }

        String key = RedisConstants.ACTIVATED_OTHER;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, curUser.getUserId().toString());
        if (Boolean.TRUE.equals(isMember)) {
            throw new BusinessException(CodeEnum.FAIL,"你已激活过其他账号");
        }

        boolean update = lambdaUpdate().eq(User::getUserName, studentNo)
                .set(User::getStatus, UserStatusEnum.NORMAL.getValue())
                .update();
        if (update) {
            // 保存已激活过其他账号状态
            stringRedisTemplate.opsForSet().add(key, curUser.getUserId().toString());
            // 赠送积分
            lambdaUpdate().eq(User::getUserId, curUser.getUserId())
                    .setSql("coin_count=coin_count+20")
                    .update();
            User user = getById(curUser.getUserId());
            this.updateCacheUser(user);
            CoinRecord coinRecord = new CoinRecord();
            coinRecord.setUserId(curUser.getUserId());
            coinRecord.setCount(20);
            coinRecord.setOperationType(CoinOperationTypeEnum.ADD.getValue());
            coinRecord.setRemain(user.getCoinCount());
            coinRecord.setNote("激活他人账号"+studentNo);
            coinRecordService.save(coinRecord);
            return true;
        }
        throw new BusinessException(CodeEnum.SYSTEM_ERROR);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activeByCoin() {
        int needCount = 50;
        User curUser = CurUserUtil.getCurUserThrow();
        if (curUser.getStatus().equals(UserStatusEnum.NORMAL.getValue())) {
            throw new BusinessException(CodeEnum.FAIL,"你的账号已激活");
        }
        if (curUser.getCoinCount() < needCount) {
            throw new BusinessException(CodeEnum.FAIL,"积分数不足"+needCount);
        }
        boolean success = lambdaUpdate().eq(User::getUserId, curUser.getUserId())
                .set(User::getStatus, UserStatusEnum.NORMAL.getValue())
                .update();
        if (success) {
            lambdaUpdate().eq(User::getUserId, curUser.getUserId())
                    .setSql("coin_count=coin_count-"+needCount)
                    .update();
            User user = getById(curUser.getUserId());
            this.updateCacheUser(user);
            CoinRecord coinRecord = new CoinRecord();
            coinRecord.setUserId(curUser.getUserId());
            coinRecord.setCount(needCount);
            coinRecord.setOperationType(CoinOperationTypeEnum.MINUS.getValue());
            coinRecord.setRemain(user.getCoinCount());
            coinRecord.setNote("激活账号");
            coinRecordService.save(coinRecord);
            return true;
        }
        throw new BusinessException(CodeEnum.SYSTEM_ERROR);
    }

    // 以下为private方法

    /**
     * 随机头像
     */
    private String randomAvatar() {
        int i = RandomUtil.randomInt(100001, 999999999);
        return "https://q.qlogo.cn/g?b=qq&nk="+i+"&s=100";
    }

    private void validateCaptcha(String code, String captchaId, boolean autoDelete) {
        String verifyKey = String.format("%s:%s", RedisConstants.CAPTCHA, captchaId);
        String captcha = stringRedisTemplate.opsForValue().get(verifyKey);
        if (captcha == null) {
            throw new BusinessException(CodeEnum.FAIL, "验证码已过期");
        }
        if (captcha.equals(code)) {
            if (autoDelete) {
                stringRedisTemplate.delete(verifyKey);
            }
        } else {
            throw new BusinessException(CodeEnum.FAIL, "验证码错误");
        }
    }

    private void updateLoginDateAndIp(Long userId) {
        // todo 采用异步
//        CompletableFuture.runAsync(() -> {
            User user = new User();
            user.setUserId(userId);
            user.setLoginDate(new Date());
            user.setLoginIp(HttpUtils.getIpAddress());
            this.updateById(user);
//        });
    }

    private User chaoXingLogin(String phone, String password) {
        ChaoXingUser chaoXingUser = ChaoXingUtil.getChaoXingUser(phone, password);
        User user = lambdaQuery().eq(User::getCxId, chaoXingUser.getCxId()).one();
        // 该用户首次登录
        if (user == null) {
            Integer fid = chaoXingUser.getFid();
            if (fid != 1250) {
                throw new BusinessException(CodeEnum.FAIL,"非浙江工业大学账号");
            }
            String xuehao = chaoXingUser.getXuehao();
            String name = chaoXingUser.getName();
            Long cxId = chaoXingUser.getCxId();
            Date acTime = chaoXingUser.getAcTime();

            // 学号已有账号
            User byUserName = getByUserName(xuehao);
            if (byUserName != null) {
                lambdaUpdate().set(User::getCxId,cxId)
                        .set(StrUtil.isEmpty(byUserName.getPhone()),User::getPhone,phone)
                        .eq(User::getUserName, xuehao).update();
            } else {
                // 学生首次进入
                User regUser = new User();
                regUser.setNickName(name);
                regUser.setUserName(xuehao);
                regUser.setPassword(password);
                regUser.setPhone(phone);
                regUser.setGrade(acTime.getYear()+1900);
                regUser.setCxId(cxId);
                regUser.setAvatar(FileConstants.DEFAULT_AVATAR);
                save(regUser);
            }
            user = lambdaQuery().eq(User::getCxId, chaoXingUser.getCxId()).one();
        }
        return user;
    }

    private User weJhLogin(String xuehao, String password) {
        WeJhUser weJhUser = WeJhUtil.getWeJhUser(xuehao, password);
        User user = lambdaQuery().eq(User::getWejhId, weJhUser.getId()).one();
        if (user == null) {
            Long id = weJhUser.getId();
            String phoneNum = weJhUser.getPhoneNum();
            String studentID = weJhUser.getStudentID();
            Date createTime = weJhUser.getCreateTime();
            // 学号已有账号
            User byUserName = getByUserName(studentID);
            if (byUserName != null) {
                lambdaUpdate().set(User::getWejhId,id).eq(User::getUserName, studentID).update();
            } else {
                // 学生首次进入
                User regUser = new User();
                regUser.setWejhId(id);
                regUser.setPhone(phoneNum);
                regUser.setUserName(studentID);
                regUser.setPassword(password);
                regUser.setNickName(studentID);
                regUser.setGrade(createTime.getYear()+1900);
                regUser.setAvatar(FileConstants.DEFAULT_AVATAR);
                save(regUser);
            }
            user = lambdaQuery().eq(User::getWejhId, weJhUser.getId()).one();
        }
        return user;
    }

    private User passwdLogin(String username, String password) {
        User user = getByUserName(username);
        if (user == null) {
            throw new BusinessException(CodeEnum.FAIL, "账号或密码错误");
        }
        // todo  md5加密
//        String md5Pw = SecureUtil.md5(password);
//        if (!md5Pw.equals(user.getPassword())) {
//            throw new BusinessException(CodeEnum.FAIL, "账号或密码错误");
//        }
        if (!Objects.equals(password, user.getPassword())) {
            throw new BusinessException(CodeEnum.FAIL, "账号或密码错误");
        }
        return user;
    }

    private String cacheUser(User user) {
        // 判断当前用户是否已经存了token
        String getUserTokenKey = String.format("%s:%s", RedisConstants.CACHE_TOKEN_KEY, user.getUserId());
        String token = stringRedisTemplate.opsForValue().get(getUserTokenKey);
        if (token!=null) {
            // 用户存过token
        } else {
            // 用户未存过token
            token = IdUtil.simpleUUID();
            stringRedisTemplate.opsForValue().set(getUserTokenKey,token);
        }
        String key = String.format("%s:%s", RedisConstants.CACHE_USER_KEY, token);
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(user), RedisConstants.cacheUserDuration);
        return token;
    }

    private void updateCacheUser(Long userId) {
        User user = getById(userId);
        updateCacheUser(user);
    }

    private User getByUserName(String username) {
        if (username == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        return this.baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, username));
    }

    private boolean updateSignCount(Long userId,Integer count) {
        return lambdaUpdate().set(User::getSignCount,count).eq(User::getUserId,userId).update();
    }

    private boolean updateCoinCount(Long userId,Integer count) {
        return lambdaUpdate().set(User::getCoinCount,count).eq(User::getUserId,userId).update();
    }

    private boolean addLevelCount(Long userId,Integer count) {
        return lambdaUpdate().eq(User::getUserId,userId).setSql("level_count=level_count+"+count).update();
    }

    private LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userQueryRequest, user);
        Long userId = user.getUserId();
        String nickName = user.getNickName();
        String userType = user.getUserType();
        String gender = user.getGender();
        String status = user.getStatus();
        String college = user.getCollege();
        String major = user.getMajor();
        Integer grade = user.getGrade();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, User::getUserId, userId);
        queryWrapper.like(StringUtils.isNotBlank(nickName), User::getNickName, nickName);
        queryWrapper.eq(userType != null, User::getUserType, userType);
        queryWrapper.eq(gender != null, User::getGender, gender);
        queryWrapper.eq(status != null, User::getStatus, status);
        queryWrapper.like(StringUtils.isNotBlank(college), User::getCollege, college);
        queryWrapper.like(StringUtils.isNotBlank(major), User::getMajor, major);
        queryWrapper.eq(grade != null, User::getGrade, grade);
        return queryWrapper;
    }
}
