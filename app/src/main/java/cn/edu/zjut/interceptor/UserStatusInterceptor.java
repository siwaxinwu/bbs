package cn.edu.zjut.interceptor;

import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.utils.CurUserUtil;
import cn.edu.zjut.userService.enums.UserStatusEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserStatusInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserDto userDto = CurUserUtil.getCurUserDto();
        if (userDto == null) {
            return true;
        }
        UserStatusEnum statusEnum = UserStatusEnum.transform(userDto.getStatus());
        if (UserStatusEnum.BLOCKED.equals(statusEnum)) {
            throw new BusinessException(CodeEnum.NOT_ALLOW,"账号已被封禁，请联系管理员");
        }
        else if (UserStatusEnum.NOT_ACTIVE.equals(statusEnum)) {
            throw new BusinessException(CodeEnum.NOT_ALLOW,"账号未激活");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
