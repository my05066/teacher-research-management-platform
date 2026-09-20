package com.tit.university.utils;

import com.tit.university.properties.BaiduOcrProperties;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 *  百度OCR图片文字识别工具类
 */
@Component
public class BaiduOcrUtil {

    private static final Logger log = LoggerFactory.getLogger(BaiduOcrUtil.class);
    @Autowired
    private BaiduOcrProperties baiduOcrProperties;



    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

    // 识别图片字节数组
    public String recognizeImageBytes(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("图片数据不能为空");
        }
        return doRecognize(imageBytes);
    }

    // 识别本地图片文件
    public String recognizeLocalImage(String imagePath) throws IOException {
        byte[] imgData = readFileBytes(imagePath);
        return doRecognize(imgData);
    }



    private String doRecognize(byte[] imageBytes) throws IOException {
        String token = getAccessToken();
        String b64Img = Base64.getEncoder().encodeToString(imageBytes);

        RequestBody form = new FormBody.Builder()
                .add("image", b64Img)
                .add("detect_direction", "false")
                .add("paragraph", "false")
                .add("probability", "false")
                .build();

        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate?access_token=" + token;
        Request req = new Request.Builder()
                .url(url)
                .post(form)
                .addHeader("Accept", "application/json")
                .build();

        try (Response resp = httpClient.newCall(req).execute()) {
            String body = resp.body() != null ? resp.body().string() : "";
            if (!resp.isSuccessful()) {
                throw new IOException("百度OCR调用失败, code=" + resp.code() + ", msg=" + body);
            }
            return body;
        }
    }

    private byte[] readFileBytes(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) throw new IOException("图片文件不存在：" + path);
        if (!f.isFile()) throw new IOException("路径不是有效文件：" + path);

        try (FileInputStream fis = new FileInputStream(f);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = fis.read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            return bos.toByteArray();
        }
    }

    // 获取百度OCR的AccessToken
    private String getAccessToken() throws IOException {
        MediaType mt = MediaType.parse("application/x-www-form-urlencoded");
        String params = "grant_type=client_credentials&client_id=" + baiduOcrProperties.getApiKey()
                + "&client_secret=" + baiduOcrProperties.getSecretKey();
        RequestBody reqBody = RequestBody.create(mt, params);

        Request req = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .post(reqBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response resp = httpClient.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("获取AccessToken失败, code=" + resp.code());
            }
            String respStr = resp.body().string();
            return new JSONObject(respStr).getString("access_token");
        }
    }
}