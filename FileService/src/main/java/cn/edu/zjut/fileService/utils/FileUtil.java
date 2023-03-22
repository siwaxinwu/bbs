package cn.edu.zjut.fileService.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author bert
 */
@Component
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "qiniuyun")
@Setter
public class FileUtil {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;

    /**
     * @param inputStream 输入刘
     * @param fileKey 文件目录及文件名 如/2022/12/abc.png
     * @return 文件链接
     */
    public String uploadFile(InputStream inputStream,String fileKey) {
        Configuration cfg = new Configuration(Region.region0());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        UploadManager uploadManager = new UploadManager(cfg);

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(inputStream, fileKey, upToken, null, null);
            return domain+ URLEncoder.encode(fileKey, StandardCharsets.UTF_8);
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        return null;
    }

}
