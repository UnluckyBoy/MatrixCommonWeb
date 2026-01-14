package com.cloudstudio.matrix.matrixcommonweb.controller;

import com.cloudstudio.matrix.matrixcommonweb.model.ImageBean;
import com.cloudstudio.matrix.matrixcommonweb.service.image.ImageService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.FileUploadUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName：ImageUploadController
 * @Author: matrix
 * @Date: 2026/1/10 12:42
 * @Description:图片处理控制器
 */
@RestController
@RequestMapping("/uploadApi")
@CrossOrigin(origins = "*")
public class ImageUploadController {

    @Value("${back-resource.dir}")
    private String uploadDir;
    private static final String feedBackImgPath="/Feedback/";
    private static final String subDir="images";

    @Autowired
    private ImageService imageService;

    private static final Gson gson=new GsonBuilder().serializeNulls().create();

    /**
     * 单张图片上传
     */
    @PostMapping("/image")
    public void uploadImage(HttpServletResponse response, @RequestParam("file") MultipartFile file) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        //response.getWriter().write(gson.toJson(imageService.uploadImage(file,uploadDir+feedBackImgPath)));
        //System.out.println(TimeUtil.GetTime(true)+" 返回参数:");
        // 先获取返回值
        Object result = imageService.uploadImage(file, uploadDir,feedBackImgPath,subDir+"/");
        String jsonResult = gson.toJson(result);
        System.out.println(TimeUtil.GetTime(true) + " 返回参数:");
        System.out.println(jsonResult);
        response.getWriter().write(jsonResult);
    }

    /**
     * 多张图片上传
     */
    @PostMapping("/images")
    public void uploadMultipleImages(HttpServletResponse response,@RequestParam("files") MultipartFile[] files) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(gson.toJson(imageService.uploadMultipleImages(files,uploadDir,feedBackImgPath,subDir+"/")));
    }
}
