package cn.edu.zjut.postService.controller;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.postService.dto.ActiveListRequest;
import cn.edu.zjut.postService.utils.ParseRequestUtils;
import cn.edu.zjut.postService.vo.Active;
import cn.edu.zjut.postService.vo.Course;
import cn.edu.zjut.postService.vo.LocationInfo;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bert
 * @date 2023/1/9 14:48
 */
@RestController
@RequestMapping("/chaoxing/active")
@Api(tags = "超星课程活动")
public class ActiveController {

    @PostMapping("/list")
    @ApiOperation("活动列表")
    public Result<List<Active>> activeList(HttpServletRequest httpServletRequest, @RequestBody ActiveListRequest request) {
        String cookie = ParseRequestUtils.getCookie(httpServletRequest);
        String url = String.format("https://mobilelearn.chaoxing.com/v2/apis/active/student/activelist" +
                        "?fid=0&courseId=%s&classId=%s&showNotStartedActive=0&_=%d",
                        request.getCourseId(),request.getClassId(),System.currentTimeMillis());
        HttpResponse response = HttpRequest.get(url)
                .cookie(cookie).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if (jsonObject.getInteger("result") != 1) {
            throw new BusinessException(CodeEnum.FAIL);
        }
        JSONArray activeListJson = jsonObject.getJSONObject("data").getJSONArray("activeList");
        int size = activeListJson.size();
        List<Active> activeList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JSONObject activeJson = activeListJson.getJSONObject(i);
            String id = activeJson.getString("id");
            Integer type = activeJson.getInteger("type");
            Integer status = activeJson.getInteger("status");
            Integer isLook = activeJson.getInteger("isLook");
            String logo = activeJson.getString("logo");
            String nameOne = activeJson.getString("nameOne");
            Long endTime = activeJson.getLong("endTime");
            String nameFour = activeJson.getString("nameFour");

            Active active = new Active();
            active.setActiveId(id);
            active.setType(type);
            active.setStatus(status);
            active.setIsLook(isLook);
            active.setName(nameOne);
            active.setLogo(logo);
            active.setEndTime(endTime);
            active.setEndTimeFriendly(nameFour);
            activeList.add(active);
        }
        return Result.ok(activeList);
    }

    @PostMapping("/info/location/{activeId}")
    @ApiOperation("获取位置签到的信息")
    public Result<LocationInfo> locationInfo(HttpServletRequest httpServletRequest, @PathVariable String activeId) {
        String cookie = ParseRequestUtils.getCookie(httpServletRequest);
        String url = String.format("https://mobilelearn.chaoxing.com/v2/apis/active/getPPTActiveInfo?activeId=%s", activeId);
        HttpResponse response = HttpRequest.get(url).cookie(cookie).execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        JSONObject data = jsonObject.getJSONObject("data");
        String locationText = data.getString("locationText");
        String latitude = data.getString("locationLatitude");
        String longitude = data.getString("locationLongitude");

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLocationText(locationText);
        locationInfo.setLocationLatitude(latitude);
        locationInfo.setLocationLongitude(longitude);
        return Result.ok(locationInfo);
    }



}
