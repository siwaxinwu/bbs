package cn.edu.zjut.postService.utils;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.ChaoXingUser;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author bert
 * @date 2023/1/20 23:18
 */
public class ParseRequestUtils {
    public static String getCookie(HttpServletRequest request) {
        String header = request.getHeader("cx-cookie");
        if (StrUtil.isEmpty(header)) {
            throw new BusinessException(CodeEnum.FAIL,"未登录超星账号");
        }
        return header;
    }

    public static ChaoXingUser getCxUser(HttpServletRequest httpServletRequest) {
        String cookie = ParseRequestUtils.getCookie(httpServletRequest);
        String url = "https://sso.chaoxing.com/apis/login/userLogin4Uname.do";
        HttpResponse response = HttpRequest.get(url).cookie(cookie).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody).getJSONObject("msg");
        if (responseBody.contains("重新登录")) {
            throw new BusinessException(CodeEnum.FAIL,"登录信息过期，请重新登录超星账号");
        }
        if (jsonObject == null) {
            throw new BusinessException(CodeEnum.FAIL,"获取用户信息失败");
        }

        // 学校id
        Integer fid = jsonObject.getInteger("fid");
        // 学号
        String xuehao = jsonObject.getString("uname");
        // 姓名
        String name = jsonObject.getString("name");
        // 电话号码
        String phone = jsonObject.getString("phone");
        // 超星uid
        Long uid = jsonObject.getLong("uid");
        // 学校名称
        String schoolName = jsonObject.getString("schoolname");
        // 入学时间
        Date acTime = jsonObject.getDate("acttime2");
        // 身份证
        String idCard = jsonObject.getJSONObject("accountInfo").getJSONObject("cx_fanya").getString("uname");

        ChaoXingUser user = new ChaoXingUser();
        user.setFid(fid);
        user.setSchoolName(schoolName);
        user.setXuehao(xuehao);
        user.setName(name);
        user.setPhone(phone);
        user.setCxId(uid);
        user.setAcTime(acTime);
        user.setIdCard(idCard);

        return user;
    }
}
