package com.cloudstudio.matrix.matrixcommonweb.model.SystemInfo;

import lombok.Data;

/**
 * @ClassName：SystemInfo
 * @Author: matrix
 * @Date: 2025/8/4 12:28
 * @Description:系统信息类
 */
@Data
public class MatrixSystemInfoBean {
    private String cpuUsage;// CPU使用率 (%)
    private String totalMemory;// 总内存 (bytes)
    private String usedMemory;// 已用内存 (bytes)
    private String totalDisk;// 总磁盘空间 (bytes)
    private String freeDisk;// 空闲磁盘空间 (bytes)

    @Override
    public String toString() {
        return  "服务器信息:"+
                "\ncpuUsage=" + cpuUsage +
                "\ntotalMemory=" + totalMemory +
                "\nusedMemory=" + usedMemory +
                "\ntotalDisk=" + totalDisk +
                "\nfreeDisk=" + freeDisk;
    }
}
