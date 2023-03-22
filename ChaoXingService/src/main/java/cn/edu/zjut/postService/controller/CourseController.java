package cn.edu.zjut.postService.controller;

import cn.edu.zjut.common.enums.CodeEnum;
import cn.edu.zjut.common.exception.BusinessException;
import cn.edu.zjut.common.model.Result;
import cn.edu.zjut.postService.utils.ParseRequestUtils;
import cn.edu.zjut.postService.vo.Course;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bert
 * @date 2023/1/9 14:48
 */
@RestController
@RequestMapping("/chaoxing/course")
@Api(tags = "超星课程")
@Slf4j
public class CourseController {

    @GetMapping("/list")
    @ApiOperation("获取课程列表")
    public Result<List<Course>> courseList(HttpServletRequest request) throws IOException {
        String cookie = ParseRequestUtils.getCookie(request);
        if (cookie == null) {
            throw new BusinessException(CodeEnum.FAIL);
        }
        String url = "http://mooc1-api.chaoxing.com/mycourse/backclazzdata";
        HttpRequest httpRequest = HttpRequest.get(url)
                .header("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 14_2_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 com.ssreader.ChaoXingStudy/ChaoXingStudy_3_4.8_ios_phone_202012052220_56 (@Kalimdor)_12787186548451577248")
                .header("Cookie",cookie);
        HttpResponse response = httpRequest.execute();
        String responseBody = response.body();
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if (jsonObject.getInteger("result") != 1) {
            throw new BusinessException(CodeEnum.FAIL);
        }
        JSONArray channelList = jsonObject.getJSONArray("channelList");
        int size = channelList.size();
        List<Course> courseList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JSONObject channelJson = channelList.getJSONObject(i);
            if (!"课程".equals(channelJson.getString("cataName")) || channelJson.getString("key").startsWith("tea")) {
                continue;
            }
            JSONObject courseJson = channelJson.getJSONObject("content").getJSONObject("course")
                                                .getJSONArray("data").getJSONObject(0);

            String  courseId = courseJson.getString("id");
            String courseName = courseJson.getString("name");
            String courseCover = courseJson.getString("imageurl");
            String teacherName = courseJson.getString("teacherfactor");
            String classId = channelJson.getString("key");
            String className = channelJson.getJSONObject("content").getString("name");

            Course course = new Course();
            course.setCourseId(courseId);
            course.setCourseName(courseName);
            course.setCourseCover(courseCover);
            course.setClassId(classId);
            course.setClassName(className);
            course.setTeacherName(teacherName);
            courseList.add(course);
        }
        return Result.ok(courseList);
    }


}
