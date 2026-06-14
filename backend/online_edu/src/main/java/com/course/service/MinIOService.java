package com.course.service;



import com.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class MinIOService {

    @Resource
    private MinioConfig minioConfig;

    @Resource
    private MinioClient minioClient;

    /**
     * 查看存储bucket是否存在
     *
     * @param bucketName 存储桶名称
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return found;
    }

    /**
     * c
     *
     * @param bucketName 存储桶名称
     * @return Boolean
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            // 创建完成后设置权限为public
            setBucketPolicy(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除存储bucket
     *
     * @param bucketName 存储桶名称
     * @return Boolean
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取全部bucket
     *
     * @return 存储桶列表
     */
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件对象名称
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();


        if (!StringUtils.hasText(originalFilename)) {
            throw new RuntimeException();
        }

        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String prefix = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String objectName = prefix + "/" + fileName;
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            // 文件名称相同会覆盖
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectName;
    }

    /**
     * 预览图片
     *
     * @param fileName 文件名称
     * @return 文件预览链接
     */
    public String preview(String fileName) {
        // 查看文件地址
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs
                .builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName).method(Method.GET).build();
        try {
            return minioClient.getPresignedObjectUrl(build);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名称
     * @param res      response
     */
    public void download(String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(minioConfig.getBucketName())
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects() {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(minioConfig.getBucketName()).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items;
    }

    /**
     * 删除
     *
     * @param fileName 文件名称
     * @return 是否删除成功
     */
    public boolean remove(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 设置桶策存储策略为public
     *
     * @param bucketName 存储桶名称
     */
    public void setBucketPolicy(String bucketName) {
        // Define the policy string
        String policy =
                "{" +
                        "  \"Version\": \"2012-10-17\"," +
                        "  \"Statement\": [" +
                        "    {" +
                        "      \"Effect\": \"Allow\"," +
                        "      \"Principal\": \"*\"," + // 关键！直接使用字符串 \"*\" 表示公开
                        "      \"Action\": [" +
                        "        \"s3:GetBucketLocation\"," +
                        "        \"s3:ListBucket\"," +
                        "        \"s3:ListBucketMultipartUploads\"" +
                        "      ]," +
                        "      \"Resource\": \"arn:aws:s3:::" + bucketName + "\"" +
                        "    }," +
                        "    {" +
                        "      \"Effect\": \"Allow\"," +
                        "      \"Principal\": \"*\"," + // 关键！公开访问
                        "      \"Action\": [" +
                        "        \"s3:AbortMultipartUpload\"," +
                        "        \"s3:DeleteObject\"," +
                        "        \"s3:GetObject\"," + // 必须包含 GetObject 才能预览
                        "        \"s3:ListMultipartUploadParts\"," +
                        "        \"s3:PutObject\"" +
                        "      ]," +
                        "      \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"" +
                        "    }" +
                        "  ]" +
                        "}";
        try {
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build();
            minioClient.setBucketPolicy(setBucketPolicyArgs);
        } catch (ErrorResponseException | io.minio.errors.ErrorResponseException | InsufficientDataException |
                 InternalException | InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException e) {
            throw new RuntimeException(e);
        }

    }


}


