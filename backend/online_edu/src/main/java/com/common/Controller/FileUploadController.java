package com.common.Controller;


import com.common.core.domain.AjaxResult;
import com.config.MinioConfig;
import com.course.service.MinIOService;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/file")
@AllArgsConstructor
public class FileUploadController {
    private MinioConfig minioConfig;
    private MinIOService minIOUtil;

    @GetMapping("/bucketExists")
    public AjaxResult bucketExists(@RequestParam("bucketName") String bucketName) {
        Map<String, Object> map = new HashMap<>();
        map.put("bucketExists", minIOUtil.bucketExists(bucketName));
        return AjaxResult.success("查询成功", map);
    }

    @GetMapping("/makeBucket")
    public AjaxResult makeBucket(@RequestParam("bucketName") String bucketName) {
        Map<String, Object> map = new HashMap<>();
        map.put("makeBucketSuccess", minIOUtil.makeBucket(bucketName));
        return AjaxResult.success("创建成功", map);
    }

    @GetMapping("/removeBucket")
    public AjaxResult removeBucket(@RequestParam("bucketName") String bucketName) {
        Map<String, Object> map = new HashMap<>();
        map.put("deleteBucketSuccess", minIOUtil.removeBucket(bucketName));
        return AjaxResult.success("删除成功", map);
    }

    @GetMapping("/getAllBuckets")
    public AjaxResult getAllBuckets() {
        Map<String, Object> map = new HashMap<>();
        map.put("buckets", minIOUtil.getAllBuckets());
        return AjaxResult.success("查询成功", map);
    }

    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        String objectName = minIOUtil.upload(file);

        if (objectName != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("fileName", objectName);
            map.put("url", (minioConfig.getUrl() + "/" + minioConfig.getBucketName() + "/" + objectName));
            return AjaxResult.success("上传成功", map);
        }
        return AjaxResult.fail("上传失败");
    }

    @GetMapping("/preview")
    public AjaxResult preview(@RequestParam("fileName") String fileName) {
        Map<String, Object> map = new HashMap<>();
        map.put("url", minIOUtil.preview(fileName));
        return AjaxResult.success("预览成功", map);
    }

    @GetMapping("/download")
    public AjaxResult download(@RequestParam("fileName") String fileName, HttpServletResponse resp) {
        minIOUtil.download(fileName, resp);
        return AjaxResult.success();
    }

    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Map<String, String> params) {
        String url = params.get("url");
        String objName = url.substring(url.lastIndexOf(minioConfig.getBucketName() + "/") + minioConfig.getBucketName().length() + 1);
        log.info("删除对象: {}", objName);

        minIOUtil.remove(objName);
        return AjaxResult.success("删除成功");
    }

    /**
     * 设置桶策存储策略为public
     */
    @GetMapping("/setBucketPolicy")
    public AjaxResult setBucketPolicy(@RequestParam("bucketName") String bucketName) {
       try {
           minIOUtil.setBucketPolicy(bucketName);
           return AjaxResult.success("设置成功");
       }catch (Exception e) {
           return AjaxResult.fail("设置失败");
       }
    }
}


