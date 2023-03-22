package cn.edu.zjut.userService.utils;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.WeJhUser;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @author bert
 * @date 2023/3/2 22:11
 */
public class WeJhUtil {
    public static String getWeJhCookie(String xuehao, String passwrod) {
        String url = "https://wejh.zjutjh.com/api/user/login";
        JSONObject body = new JSONObject();
        body.put("username",xuehao);
        body.put("password",passwrod);
        HttpResponse response = HttpRequest.post(url).body(body.toJSONString()).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if (jsonObject.getInteger("code") != 1) {
            throw new BusinessException(CodeEnum.FAIL,jsonObject.getString("msg"));
        }
        return response.header("set-cookie");
    }

    public static WeJhUser getWeJhUser(String xuehao, String passwrod) {
        String cookie = getWeJhCookie(xuehao, passwrod);
        String url = "https://wejh.zjutjh.com/api/user/info";
        HttpResponse response = HttpRequest.post(url).cookie(cookie).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if (jsonObject.getInteger("code") != 1) {
            throw new BusinessException(CodeEnum.FAIL,jsonObject.getString("msg"));
        }

        JSONObject userJson = jsonObject.getJSONObject("data").getJSONObject("user");
        Date createTime = userJson.getDate("createTime");
        String phoneNum = userJson.getString("phoneNum");
        String studentID = userJson.getString("studentID");
        Integer userType = userJson.getInteger("userType");
        String username = userJson.getString("username");
        Long id = userJson.getLong("id");

        WeJhUser weJhUser = new WeJhUser();
        weJhUser.setId(id);
        weJhUser.setPhoneNum(phoneNum);
        weJhUser.setStudentID(studentID);
        weJhUser.setUsername(username);
        weJhUser.setUserType(userType);
        weJhUser.setCreateTime(createTime);
        return weJhUser;
    }
}
