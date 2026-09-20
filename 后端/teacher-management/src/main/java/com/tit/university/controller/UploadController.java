package com.tit.university.controller;

import com.tit.university.pojo.Result;
import com.tit.university.service.DeepSeekService;
import com.tit.university.utils.AliOssUtil;
import com.tit.university.utils.BaiduOcrUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@Slf4j
@RequestMapping("/upload")
public class UploadController {


    @Autowired
    private BaiduOcrUtil baiduOcrUtil;

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private AliOssUtil aliOssUtil;



    @Value("${file.upload.path:./uploads}")
    private String uploadPath;


    // 快速录入：上传证书图片 -> OCR -> AI分类 -> 返回结构化数据
    @PostMapping("/quick-entry")
    public Result<Map<String, Object>> quickEntry(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的证书图片");
        }
        String origName = file.getOriginalFilename();
        if (origName != null && !origName.matches("(?i).*\\.(jpg|jpeg|png|webp|bmp)$")) {
            return Result.error("请上传图片格式文件（jpg/png/webp等）");
        }

        String ossUrl = "";
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
            String ext = origName.substring(origName.lastIndexOf("."));
            String objName = UUID.randomUUID() + ext;
            ossUrl = aliOssUtil.upload(fileBytes, objName);
        } catch (IOException e) {
            log.error("文件上传OSS失败", e);
            return Result.error("文件上传失败");
        }

        try {
            // OCR识别
            String ocrRaw = baiduOcrUtil.recognizeImageBytes(fileBytes);
            String ocrText = parseOcrText(ocrRaw);

            if (ocrText == null || ocrText.isBlank()) {
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("type", "unknown");
                fallback.put("data", Map.of());
                fallback.put("certificatePath", ossUrl);
                fallback.put("certificateName", origName);
                fallback.put("certificateSize", file.getSize());
                return Result.success(fallback);
            }

            // 调用模型分类提取
            Map<String, Object> extracted = deepSeekService.classifyAndExtract(ocrText);
            extracted.put("certificatePath", ossUrl);
            extracted.put("certificateName", origName);
            extracted.put("certificateSize", file.getSize());
            return Result.success(extracted);
        } catch (Exception e) {
            log.error("快速录入失败", e);
            return Result.error("处理失败：" + e.getMessage());
        }
    }

    // 从百度OCR的JSON里拼出纯文本
    private String parseOcrText(String ocrJson) {
        try {
            JSONObject obj = new JSONObject(ocrJson);
            if (obj.has("error_code")) {
                log.warn("百度OCR返回错误: {}", ocrJson);
                return "";
            }
            JSONArray words = obj.optJSONArray("words_result");
            if (words == null) return "";

            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < words.length(); i++) {
                JSONObject w = words.optJSONObject(i);
                if (w != null && w.has("words")) {
                    buf.append(w.getString("words")).append("\n");
                }
            }
            return buf.toString().trim();
        } catch (Exception e) {
            log.warn("解析OCR结果异常", e);
            return "";
        }
    }




}
