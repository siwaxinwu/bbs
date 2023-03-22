package cn.edu.zjut.userService.service.impl;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.zjut.userService.model.entity.UserTag;
import cn.edu.zjut.userService.service.UserTagService;
import cn.edu.zjut.userService.mapper.UserTagMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author bert
* @description 针对表【user_tag(用户标签表)】的数据库操作Service实现
* @createDate 2023-01-12 11:28:50
*/
@Service
public class UserTagServiceImpl extends ServiceImpl<UserTagMapper, UserTag>
    implements UserTagService{

    @Override
    public List<UserTag> getUserTagListByUid(Long uid) {
        if (uid == null || uid <0 ) {
            throw new BusinessException(CodeEnum.PARAMS_ERROR);
        }
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId,uid);
        return this.baseMapper.selectList(wrapper);
    }
}




