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
 * @description 位置签到
 * @date 2023/2/20 20:30
 */
@Component
public class LocationSign implements BasicSign{

    @Override
    public boolean doSign(SignRequest request) {
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String cookie = ParseRequestUtils.getCookie(httpServletRequest);
        String url = String.format("https://mobilelearn.chaoxing.com/pptSign/stuSignajax" +
                        "?address=%s&activeId=%s&latitude=%s&longitude=%s&fid=0&appType=15&ifTiJiao=1",
                request.getLocationText(),request.getActiveId(),request.getLocationLatitude(),request.getLocationLongitude());
        HttpResponse response = HttpRequest.get(url).cookie(cookie).execute();
        String responseBody = response.body();
        if (!"success".equals(responseBody)) {
            throw new BusinessException(CodeEnum.FAIL,responseBody);
        }
        return true;
    }

    @Override
    public boolean isSupport(Integer typeCode) {
        return typeCode.equals(2);
    }
}
