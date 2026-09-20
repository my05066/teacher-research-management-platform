package com.tit.university.service.impl;

import com.tit.university.service.DeepSeekService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// 调用DeepSeek模型实现OCR文本分类和提取
@Slf4j
@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private static final String DS_API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Value("${deepseek.api-key:}")
    private String apiKey;

    private final OkHttpClient okClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String chat(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("deepseek api-key未配置");
            return "抱歉，系统暂未配置大模型密钥，请联系管理员配置 deepseek.api-key。";
        }
        if (prompt == null || prompt.isBlank()) {
            return "问题不能为空。";
        }
        try {
            return doRequest(prompt);
        } catch (Exception e) {
            log.error("调用DeepSeek失败", e);
            return "抱歉，调用大模型服务出现异常，请稍后再试。错误信息：" + e.getMessage();
        }
    }

    @Override
    public Map<String, Object> classifyAndExtract(String ocrText) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("deepseek api-key未配置，无法进行识别");
            return makeFallback("unknown", new HashMap<>());
        }
        if (ocrText == null || ocrText.isBlank()) {
            return makeFallback("unknown", new HashMap<>());
        }
        try {
            String resp = doRequest(buildClassifyPrompt(ocrText));
            return parseAiResponse(resp);
        } catch (Exception e) {
            log.error("OCR文本分类失败", e);
            return makeFallback("unknown", Map.of("rawText", ocrText));
        }
    }

    private String buildClassifyPrompt(String ocrText) {
        return """
            你是一个科研证书信息提取助手。下面是从图片中识别出的文字（可能包含噪声或识别错误），请分析并判断证书类型，提取结构化信息。
            
            证书类型只能是以下之一：essay(发表论文)、patent(专利成果)、award(科研获奖)、meeting(学术会议)
            
            根据类型提取对应字段（保留原始识别内容，可做合理推断）：
            - essay: title(论文题目), level(论文等级如SCI一区/EI等), authorType(第一作者/通讯作者), journalName(期刊名称), publishTime(YYYY-MM-DD格式)
            - patent: name(专利名称), type(专利/专著/软著), approvalTime(获批时间YYYY-MM-DD), patentNumber(专利号/证书号)
            - award: awardName(获奖名称), level(获奖级别), awardTime(获奖时间YYYY-MM-DD), awardOrg(颁奖机构)
            - meeting: meetingName(会议名称), category(国际学术会议/国内学术会议), hasReport(1或0), reportTime(报告时间), meetingTime(会议时间), meetingLocation(会议地点)
            
            必须严格返回一个 JSON 对象，格式如下，不要包含其他文字：
            {"type":"类型英文","data":{字段键值对}}
            
            识别文字：
            %s
            """.formatted(ocrText);
    }

    private String doRequest(String prompt) throws IOException {
        JSONObject reqBody = new JSONObject();
        reqBody.put("model", "deepseek-chat");
        reqBody.put("max_tokens", 1024);
        reqBody.put("temperature", 0.2);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        JSONArray msgArr = new JSONArray();
        msgArr.put(userMsg);
        reqBody.put("messages", msgArr);

        RequestBody httpBody = RequestBody.create(
                reqBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );
        Request req = new Request.Builder()
                .url(DS_API_URL)
                .post(httpBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response resp = okClient.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("DeepSeek接口返回异常, code=" + resp.code());
            }
            String respStr = resp.body().string();
            JSONObject respJson = new JSONObject(respStr);
            JSONArray choices = respJson.getJSONArray("choices");
            if (choices.isEmpty()) {
                throw new IOException("模型返回空结果");
            }
            return choices.getJSONObject(0).getJSONObject("message").getString("content");
        }
    }

    // 解析模型返回的JSON
    private Map<String, Object> parseAiResponse(String content) {
        try {
            String text = content.trim();
            // 去掉markdown代码块包裹
            if (text.startsWith("```")) {
                int s = text.indexOf("\n") + 1;
                int e = text.lastIndexOf("```");
                text = text.substring(s, e > 0 ? e : text.length()).trim();
            }

            JSONObject obj = new JSONObject(text);
            String type = obj.optString("type", "unknown").toLowerCase();
            if (!type.matches("essay|patent|award|meeting")) {
                type = "unknown";
            }
            Map<String, Object> data = new HashMap<>();
            JSONObject dataObj = obj.optJSONObject("data");
            if (dataObj != null) {
                for (String k : dataObj.keySet()) {
                    if (dataObj.opt(k) != null) {
                        data.put(k, dataObj.opt(k));
                    }

                }
            }


            return makeResult(type, data);
        } catch (Exception e) {
            log.warn("解析模型JSON失败: {}", content, e);
            return makeFallback("unknown", Map.of("rawText", content));
        }
    }

    private Map<String, Object> makeResult(String type, Map<String, Object> data) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("data", data);
        return map;
    }

    private Map<String, Object> makeFallback(String type, Map<String, Object> data) {
        return makeResult(type, data);
    }
}
