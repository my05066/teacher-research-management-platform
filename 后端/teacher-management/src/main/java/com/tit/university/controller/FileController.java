package com.tit.university.controller;

import com.tit.university.pojo.Result;
import com.tit.university.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private AliOssUtil aliOssUtil;


    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传请求");
        try {
            String origName = file.getOriginalFilename();
            String ext = origName.substring(origName.lastIndexOf("."));
            String objName = UUID.randomUUID() + ext;
            String url = aliOssUtil.upload(file.getBytes(), objName);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
        }
        return Result.error("上传失败");
    }


}



