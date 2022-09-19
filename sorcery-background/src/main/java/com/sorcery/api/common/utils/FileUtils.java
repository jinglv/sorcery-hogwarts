package com.sorcery.api.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

/**
 * 文件上传工具类
 *
 * @author jinglv
 * @date 2022/9/16 16:28
 */
@Slf4j
@Component
public class FileUtils {
    /**
     * 图片物理存储路径
     */
    @Value("${SavePath.ProfilePhoto}")
    private String profilePhotoPath;
    /**
     * 图片映射路径
     */
    @Value("${SavePath.ProfilePhotoMapper}")
    private String profilePhotoMapperPath;

    public String uploads(MultipartFile file) {
        // 获取文件名称
        String fileName = file.getOriginalFilename();
        //获取文件后缀名。也可以在这里添加判断语句，规定特定格式的图片才能上传，否则拒绝保存。
        String suffixName = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
        // 上传后新生成文件名称
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffixName;
        // 文件写入
        try {
            //将图片保存到文件夹里
            file.transferTo(new File(profilePhotoPath + newFileName));
            return profilePhotoMapperPath + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "图片保存失败";
        }
    }
}
