package com.tit.university.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "tmp.baiduocr")
public class BaiduOcrProperties {
    private String apiKey;
    private String secretKey;
}
