package com.cloudstudio.matrix.matrixcommonweb.service.image.Impl;

import com.cloudstudio.matrix.matrixcommonweb.service.image.ImageService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.FileUploadUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.StringUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName：ImageServiceIpml
 * @Author: matrix
 * @Date: 2026/1/10 13:03
 * @Description:图片服务实现类
 */
@Service("ImageService")
public class ImageServiceImpl implements ImageService {

    /**
     * 上传图片实现
     * @param file
     * @param uploadDir
     * @return
     */
    @Override
    public WebServerResponse uploadImage(MultipartFile file,String uploadDir,String feedBackImgPath,String subDir) {
        try {
            if (file.isEmpty()) {
                System.out.println(TimeUtil.GetTime(true)+" 文件为空");
                return WebServerResponse.failure("文件为空");
            }
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null ||!(contentType.equals("image/jpeg") ||
                    contentType.equals("image/png") ||contentType.equals("image/gif") ||
                    contentType.equals("image/webp") ||contentType.equals("image/bmp"))) {
                System.out.println(TimeUtil.GetTime(true)+" 只支持JPEG、PNG、GIF、WEBP、BMP格式的图片");
                return WebServerResponse.winning("只支持JPEG、PNG、GIF、WEBP、BMP格式的图片");
            }
            // 上传文件
            String filePath = FileUploadUtil.uploadFile(file, uploadDir, feedBackImgPath+subDir);
            // 返回访问URL
            String accessUrl = "/image"+feedBackImgPath+subDir+ filePath.substring(filePath.lastIndexOf(subDir.replace("/","")) + 7);
            Map<String,Object> resultBody=new HashMap<>();
            resultBody.put("url", StringUtil.getUrlPath(accessUrl));
//            result.put("success", true);
//            result.put("message", "上传成功");
//            result.put("filePath", filePath);
//            result.put("url", accessUrl);
//            result.put("fileName", file.getOriginalFilename());
            System.out.println(TimeUtil.GetTime(true)+" 上传成功");
            return WebServerResponse.success("上传成功",resultBody);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(TimeUtil.GetTime(true)+" 上传异常");
            return WebServerResponse.failure("上传异常--->>>"+e.getMessage());
        }
    }

    /***
     * 批量上传
     * @param files
     * @param uploadDir
     * @return
     */
    @Override
    public WebServerResponse uploadMultipleImages(MultipartFile[] files, String uploadDir,String feedBackImgPath,String subDir) {
        Map<String, String> uploadedFiles = new HashMap<>();
        boolean allSuccess = true;

        for (MultipartFile file : files) {
            try {
                String filePath = FileUploadUtil.uploadFile(file, uploadDir, feedBackImgPath+subDir);
                String accessUrl = "/image"+feedBackImgPath+subDir+ filePath.substring(filePath.lastIndexOf(subDir.replace("/","")) + 7);
                uploadedFiles.put(file.getOriginalFilename(), accessUrl);
            } catch (Exception e) {
                allSuccess = false;
                uploadedFiles.put(file.getOriginalFilename(), "上传失败: " + e.getMessage());
            }
        }
        if(allSuccess){
            return WebServerResponse.success("上传成功",uploadedFiles);
        }else{
            return WebServerResponse.winning("上传异常");
        }
    }
}
