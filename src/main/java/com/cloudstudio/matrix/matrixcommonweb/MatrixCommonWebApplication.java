package com.cloudstudio.matrix.matrixcommonweb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MatrixCommonWebApplication {
    private static final Logger logger = LogManager.getLogger(MatrixCommonWebApplication.class);
    // 静态初始化块，用于在应用启动时记录日志
    static {
        logger.info("MatrixCommonWeb服务启动中...");
    }


    public static void main(String[] args) {
        SpringApplication.run(MatrixCommonWebApplication.class, args);
        logger.info("MatrixCommonWeb服务启动成功!");
    }

}
