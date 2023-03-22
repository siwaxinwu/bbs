package cn.edu.zjut.postService.controller;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.postService.dto.SignRequest;
import cn.edu.zjut.postService.sign.BasicSign;
import cn.edu.zjut.postService.utils.ParseRequestUtils;
import cn.edu.zjut.common.model.ChaoXingUser;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author bert
 * @date 2023/1/9 14:48
 */
@RestController
@RequestMapping("/chaoxing/sign")
@Api(tags = "超星课程签到")
public class SignController {

    @Resource
    private List<BasicSign> signList;

    @PostMapping
    @ApiOperation("签到")
    public Result<Boolean> signList(HttpServletRequest httpServletRequest, @RequestBody SignRequest request) {
        ChaoXingUser user = ParseRequestUtils.getCxUser(httpServletRequest);
        if (!preSign(httpServletRequest,user.getCxId(),request)) {
            throw new BusinessException(CodeEnum.FAIL);
        }

        for (BasicSign sign : signList) {
            if (sign.isSupport(request.getTypeCode())) {
                sign.preSign();
                if (!sign.doSign(request)) {
                    return Result.fail();
                }
                sign.afterSign();
                return Result.ok();
            }
        }
        return Result.fail();
    }


    /**
     * 所有签到都要进行预签到
     */
    private boolean preSign(HttpServletRequest httpServletRequest,Long uid, SignRequest request) {
        String cookie = ParseRequestUtils.getCookie(httpServletRequest);
        String url = String.format("https://mobilelearn.chaoxing.com/newsign/preSign" +
                        "?courseId=%s&classId=%s&activePrimaryId=%s&general=1&sys=1&ls=1&appType=15&tid=&uid=%s&ut=s",
                request.getCourseId(),request.getClassId(),request.getActiveId(),uid);
        HttpResponse response = HttpRequest.get(url).cookie(cookie).execute();
        String responseBody = response.body();
        return responseBody != null;
    }


}
