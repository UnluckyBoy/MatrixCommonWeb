package com.cloudstudio.matrix.matrixcommonweb.service.image;

import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName：ImageService
 * @Author: matrix
 * @Date: 2026/1/10 13:03
 * @Description:图片服务接口
 */
public interface ImageService {
    WebServerResponse uploadImage(MultipartFile file,String uploadDir,String feedBackImgPath,String subDir);
    WebServerResponse uploadMultipleImages(MultipartFile[] files,String uploadDir,String feedBackImgPath,String subDir);
}
