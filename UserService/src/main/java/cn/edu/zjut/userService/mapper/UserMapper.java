package cn.edu.zjut.userService.mapper;

import cn.edu.zjut.userService.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author bert
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-01-09 17:06:22
* @Entity cn.edu.zjut.userService.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

}
