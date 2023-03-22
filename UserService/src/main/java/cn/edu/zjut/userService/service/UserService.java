package cn.edu.zjut.userService.service;

import cn.edu.zjut.userService.model.dto.LoginDto;
import cn.edu.zjut.userService.model.dto.RegisterDto;
import cn.edu.zjut.userService.model.dto.user.UserQueryRequest;
import cn.edu.zjut.userService.model.dto.user.UserUpdateRequest;
import cn.edu.zjut.userService.model.entity.User;
import cn.edu.zjut.userService.model.vo.UserSimpleVo;
import cn.edu.zjut.userService.model.vo.UserBasicInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author bert
 */
public interface UserService extends IService<User> {
    /**
     * 条件查询用户
     * @param userQueryRequest 条件
     * @return 分页对象
     */
    Page<User> page(UserQueryRequest userQueryRequest);

    /**
     * 用户登录
     * @param loginDto loginDto
     * @return LoginUserVo
     */
    UserSimpleVo login(LoginDto loginDto);

    /**
     * 用户登录
     * @param registerDto registerDto
     * @return LoginUserVo
     */
    UserSimpleVo register(RegisterDto registerDto);

    /**
     * 当前登录用户信息
     * @return loginUserVo
     */
    UserBasicInfoVo currentLoginUserVo();

    /**
     * 获取某用户基础信息
     * @param uid uid
     * @return userBasicInfo
     */
    UserBasicInfoVo getUserBasicInfoVo(long uid);

    UserBasicInfoVo toUserBasicInfoVo(User user);

    /**
     * 将用户转换成简单对象
     * @param uid uid
     * @return UserSimpleVo
     */
    UserSimpleVo getUserSimpleVo(Long uid);

    /**
     * 将用户转换成简单对象
     * @param user user
     * @return UserSimpleVo
     */
    UserSimpleVo toUserSimpleVo(User user);

    boolean update(UserUpdateRequest request);

    /**
     * 签到
     */
    Boolean sign();

    /**
     * 更新缓存
     */
    void updateCacheUser(User user);

    boolean activeOther(String studentNo);

    boolean activeByCoin();

}
