package com.cloudstudio.matrix.matrixcommonweb.service.Impl.datahandleImpl;

import com.cloudstudio.matrix.matrixcommonweb.model.SystemInfo.MatrixSystemInfoBean;
import com.cloudstudio.matrix.matrixcommonweb.service.SystemInfo.SystemMonitorService;
import com.cloudstudio.matrix.matrixcommonweb.webtool.TimeUtil;
import com.cloudstudio.matrix.matrixcommonweb.webtool.WebServerResponse;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.util.Util;

import java.util.List;

/**
 * @ClassName：SystemMonitorServiceImpl
 * @Author: matrix
 * @Date: 2025/8/4 12:32
 * @Description:系统信息服务实现类
 */
@Service("SystemMonitorService")
public class SystemMonitorServiceImpl implements SystemMonitorService {

    @Override
    public WebServerResponse getSystemInfo() {
        MatrixSystemInfoBean systemInfo = new MatrixSystemInfoBean();
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor = hal.getProcessor();

        // 获取 CPU 型号
        String processorName = processor.getProcessorIdentifier().getName();
        // 获取 CPU 使用率（阻塞 1 秒）
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        systemInfo.setCpuUsage(String.format("%.2f", cpuUsage)+"%");

        GlobalMemory memory = hal.getMemory();
        // 物理内存
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        double totalMemoryG = totalMemory / (1024.0 * 1024 * 1024);
        double usedMemoryG = usedMemory / (1024.0 * 1024 * 1024);
        // 交换空间
        //long totalSwap = memory.getTotal();
        systemInfo.setTotalMemory(String.format("%.2f", totalMemoryG)+"G");
        systemInfo.setUsedMemory(String.format("%.2f", usedMemoryG)+"G");

        List<OSFileStore> fileStores = si.getOperatingSystem().getFileSystem().getFileStores();
        for (OSFileStore fs : fileStores) {
            String name = fs.getName();
            String mount = fs.getMount();
            long totalSpace = fs.getTotalSpace();
            long freeSpace = fs.getFreeSpace();
            long usableSpace = fs.getUsableSpace();
            double usage = (totalSpace - freeSpace) * 100.0 / totalSpace;// 计算使用率
            double totalSpaceG = totalSpace/ (1024.0 * 1024 * 1024);
            double freeSpaceG = freeSpace/ (1024.0 * 1024 * 1024);
            systemInfo.setTotalDisk(String.format("%.2f", totalSpaceG)+"G");
            systemInfo.setFreeDisk(String.format("%.2f", freeSpaceG)+"G");
        }
        System.out.println(TimeUtil.GetTime(true)+" ---服务器信息:"+systemInfo.toString());
        return WebServerResponse.success(systemInfo);
    }
}
