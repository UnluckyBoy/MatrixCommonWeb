package com.cloudstudio.matrix.matrixcommonweb.webtool;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @ClassName：FileUploadUtil
 * @Author: matrix
 * @Date: 2026/1/10 12:50
 * @Description:图片类工具
 */
public class FileUploadUtil {
    /**
     * 上传文件
     * @param file 文件对象
     * @param baseDir 基础目录
     * @param subDir 子目录（如：images, documents等）
     * @return 文件的完整路径
     * @throws IOException
     */
    public static String uploadFile(MultipartFile file, String baseDir, String subDir) throws IOException {
        // 确保基础目录存在
        File baseDirectory = new File(baseDir);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        // 创建按日期分组的子目录
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String uploadDir = baseDir + subDir + File.separator + datePath + File.separator;

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成唯一文件名，防止覆盖
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + fileExtension;

        // 保存文件
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 验证文件大小
     */
    public static boolean validateFileSize(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }

    /**
     * 压缩图片
     * @param sourcePath 源图片路径
     * @param targetPath 目标图片路径
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @param quality 质量（0-1）
     * @return 是否成功
     */
    public static boolean compressImage(String sourcePath, String targetPath,
                                        int maxWidth, int maxHeight, float quality) {
        try {
            File sourceFile = new File(sourcePath);
            BufferedImage sourceImage = ImageIO.read(sourceFile);

            // 计算压缩后的尺寸
            int originalWidth = sourceImage.getWidth();
            int originalHeight = sourceImage.getHeight();

            int newWidth = originalWidth;
            int newHeight = originalHeight;

            if (originalWidth > maxWidth) {
                newWidth = maxWidth;
                newHeight = (int) ((double) originalHeight * maxWidth / originalWidth);
            }

            if (newHeight > maxHeight) {
                newHeight = maxHeight;
                newWidth = (int) ((double) originalWidth * maxHeight / originalHeight);
            }

            // 创建压缩后的图片
            BufferedImage compressedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = compressedImage.createGraphics();
            g2d.drawImage(sourceImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();

            // 保存压缩后的图片
            String formatName = getFormatName(sourcePath);
            ImageIO.write(compressedImage, formatName, new File(targetPath));

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 获取图片格式
     * @param filePath
     * @return
     */
    public static String getFormatName(String filePath) {
        if (filePath.toLowerCase().endsWith(".png")) {
            return "png";
        } else if (filePath.toLowerCase().endsWith(".gif")) {
            return "gif";
        } else {
            return "jpg";
        }
    }
}
