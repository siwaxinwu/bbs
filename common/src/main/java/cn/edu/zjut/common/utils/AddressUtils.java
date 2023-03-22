package cn.edu.zjut.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * 获取地址类
 * 
 * @author ruoyi
 */
public class AddressUtils
{
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);
    /**
     * IP地址查询api
     */
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = null;

    public static String getRealAddressByIp(String ip)
    {
        if (ip == null) {
            return UNKNOWN;
        }
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("ip", ip);
        paramMap.put("json","true");
        String rspStr = HttpUtil.get(IP_URL, paramMap);
        if (StrUtil.isEmpty(rspStr))
        {
            log.error("获取地理位置异常 {}", ip);
            return UNKNOWN;
        }
        JSONObject obj = JSON.parseObject(rspStr);
        String region = obj.getString("pro");
        String city = obj.getString("city");
        return String.format("%s %s", region, city);
    }
}
