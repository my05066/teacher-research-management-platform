package com.tit.university.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.tit.university.properties.AliOssProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;


/**
 * 阿里云OSS对象存储工具
 */
@Data
@AllArgsConstructor
@Slf4j
@Component
public class AliOssUtil {


    @Autowired
    private AliOssProperties aliOssProperties;

    // 上传文件到OSS，返回访问URL
    public String upload(byte[] data, String objectName) {
        OSS oss = new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret());
        try {
            oss.putObject(aliOssProperties.getBucketName(), objectName, new ByteArrayInputStream(data));
        } catch (OSSException oe) {
            log.error("OSS上传异常: {}", oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("OSS客户端异常: {}", ce.getMessage());
        } finally {
            oss.shutdown();
        }

        String fileUrl = "https://" + aliOssProperties.getBucketName() + "."
                + aliOssProperties.getEndpoint() + "/" + objectName;
        log.info("文件上传完成: {}", fileUrl);
        return fileUrl;
    }
}


