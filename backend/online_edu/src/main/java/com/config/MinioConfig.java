package com.config;


import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    /**
     * 服务地址
     */
    private String url;

    /**
     * 用户名
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;



    @Bean
    public MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(url)
                // 设置访问密钥,返回的预览地址带有签名
//                .credentials(accessKey, secretKey)
                .build();
    }
}



