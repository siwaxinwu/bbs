package cn.edu.zjut.common.aspect;


import cn.edu.zjut.common.dto.UserDto;
import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.enums.user.UserTypeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.utils.CurUserUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author bert
 */
@Aspect
@Component
public class WebAdminAspect {

    @Pointcut("@annotation(cn.edu.zjut.common.annotation.RequireAdmin)")
    public void logPointCut() {
    }

    @Before("logPointCut()")
    public void hasPermission() {
        processHasPermission();
    }

    private void processHasPermission() {
        UserDto user = CurUserUtil.getCurUserDtoThrow();
        if (!user.getUserType().equals(UserTypeEnum.ADMIN.getValue())) {
            throw new BusinessException(CodeEnum.NOT_ALLOW);
        }
    }
}
