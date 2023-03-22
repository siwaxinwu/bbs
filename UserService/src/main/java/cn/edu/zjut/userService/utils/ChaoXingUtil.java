package cn.edu.zjut.userService.utils;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.ChaoXingUser;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * @author bert
 * @date 2023/3/2 21:54
 */
public class ChaoXingUtil {

    public static String getCookie(String phone, String password) {
        String url = String.format("https://passport2-api.chaoxing.com/v11/loginregister" +
                        "?code=%s&cx_xxt_passport=json&uname=%s&loginType=1&roleSelect=true",
                        password,phone);
        HttpResponse response = HttpRequest.get(url).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        StringBuilder cookies = new StringBuilder();
        if (jsonObject.getBoolean("status")) {
            List<String> list = response.headerList("Set-Cookie");
            String uid = null;
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    String temp = list.get(i).split(";")[0];
                    if (temp.startsWith("UID")) {
                        uid = temp.substring(4);
                    }
                    if (temp.startsWith("JSESSIONID")) {
                        continue;
                    }
                    cookies.append(temp).append(";");
                }
            } else {
                System.out.println("Cookies获取失败");
            }
        }
        else {
            throw new BusinessException(CodeEnum.FAIL,jsonObject.getString("mes"));
        }
        return cookies.toString();
    }

    public static ChaoXingUser getChaoXingUser(String cookie) {
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

    public static ChaoXingUser getChaoXingUser(String phone,String password) {
        String cookie = getCookie(phone, password);
        return getChaoXingUser(cookie);
    }
}
