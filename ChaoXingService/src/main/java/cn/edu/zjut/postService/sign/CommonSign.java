package cn.edu.zjut.postService.sign;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.postService.dto.SignRequest;
import cn.edu.zjut.postService.utils.ParseRequestUtils;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bert
 * @description 通用签到，包括普通签到、手势签到、签到码签到（都可以调用用一个接口来签到）
 * @date 2023/2/20 20:30
 */
@Component
public class CommonSign implements BasicSign{

    @Override
    public boolean doSign(SignRequest request) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String cookie = ParseRequestUtils.getCookie(httpServletRequest);
        String url = String.format("https://mobilelearn.chaoxing.com/pptSign/stuSignajax?activeId=%s",
                request.getActiveId());
        HttpResponse response = HttpRequest.get(url).cookie(cookie).execute();
        String responseBody = response.body();
        if (!"success".equals(responseBody)) {
            throw new BusinessException(CodeEnum.FAIL,responseBody);
        }
        return true;
    }

    @Override
    public boolean isSupport(Integer typeCode) {
        return typeCode == null || typeCode.equals(0);
    }
}
