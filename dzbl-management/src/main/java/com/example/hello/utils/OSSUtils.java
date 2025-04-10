package com.example.hello.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.example.hello.config.OSSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class OSSUtils {
    @Autowired
    private OSSConfig ossConfig;

    /**
     * 上传文件到OSS
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 避免文件覆盖
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

        ossClient.putObject(ossConfig.getBucketName(), fileName, inputStream);

        //文件访问路径
        String url = ossConfig.getEndpoint().split("//")[0] + "//" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint().split("//")[1] + "/" + fileName;
        
        // 关闭ossClient
        ossClient.shutdown();
        
        return url;
    }
} 