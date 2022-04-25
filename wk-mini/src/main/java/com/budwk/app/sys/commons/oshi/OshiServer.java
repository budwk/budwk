package com.budwk.app.sys.commons.oshi;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import org.nutz.lang.util.NutMap;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author wizzer@qq.com
 */
public class OshiServer {
    private static final int OSHI_WAIT_SECOND = 1000;

    public static NutMap getCpu(CentralProcessor processor) {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long free = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long total = user + nice + sys + free + iowait + irq + softirq + steal;
        return NutMap.NEW().addv("cpuNum", processor.getLogicalProcessorCount())
                .addv("total", NumberUtil.round(NumberUtil.mul(total, 100), 2))
                .addv("sys", NumberUtil.round(NumberUtil.mul(NumberUtil.div(sys, total), 100), 2))
                .addv("used", NumberUtil.round(NumberUtil.mul(NumberUtil.div(user, total), 100), 2))
                .addv("wait", NumberUtil.round(NumberUtil.mul(NumberUtil.div(iowait, total), 100), 2))
                .addv("free", NumberUtil.round(NumberUtil.mul(NumberUtil.div(free, total), 100), 2));
    }

    public static NutMap getMemInfo(GlobalMemory memory) {
        return NutMap.NEW().addv("total", NumberUtil.div(memory.getTotal(), (1024 * 1024 * 1024), 2))
                .addv("used", NumberUtil.div((memory.getTotal() - memory.getAvailable()), (1024 * 1024 * 1024), 2))
                .addv("free", NumberUtil.div(memory.getAvailable(), (1024 * 1024 * 1024), 2))
                .addv("usage", NumberUtil.mul(NumberUtil.div((memory.getTotal() - memory.getAvailable()), memory.getTotal(), 4), 100));
    }

    public static NutMap getSysInfo() {
        Properties props = System.getProperties();
        String hostName = "";
        String hostIp = "127.0.0.1";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            hostIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {

        }
        return NutMap.NEW().addv("hostName", hostName)
                .addv("hostIp", hostIp)
                .addv("osName", props.getProperty("os.name"))
                .addv("osArch", props.getProperty("os.arch"))
                .addv("userDir", props.getProperty("user.dir"));
    }

    public static NutMap getJvmInfo() {
        Properties props = System.getProperties();
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long max = Runtime.getRuntime().maxMemory();
        BetweenFormatter betweenFormatter = new BetweenFormatter(System.currentTimeMillis() - time, BetweenFormatter.Level.SECOND);
        return NutMap.NEW().addv("total", NumberUtil.div(total, (1024 * 1024), 2))
                .addv("max", NumberUtil.div(max, (1024 * 1024), 2))
                .addv("free", NumberUtil.div(free, (1024 * 1024), 2))
                .addv("used", NumberUtil.div(total - free, (1024 * 1024), 2))
                .addv("usage", NumberUtil.mul(NumberUtil.div(total - free, total, 4), 100))
                .addv("version", props.getProperty("java.version"))
                .addv("home", props.getProperty("java.home"))
                .addv("name", ManagementFactory.getRuntimeMXBean().getVmName())
                .addv("startTime", DateUtil.format(new Date(time), "yyyy-MM-dd HH:mm:ss"))
                .addv("runTime", betweenFormatter.format());
    }

    public static List<NutMap> getSysFiles(OperatingSystem os) {
        List<NutMap> sysFiles = new ArrayList<>();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            NutMap sysFile = NutMap.NEW();
            sysFile.addv("dirName", fs.getMount());
            sysFile.addv("sysTypeName", fs.getType());
            sysFile.addv("typeName", fs.getName());
            sysFile.addv("total", convertFileSize(total));
            sysFile.addv("free", convertFileSize(free));
            sysFile.addv("used", convertFileSize(used));
            sysFile.addv("usage", NumberUtil.mul(NumberUtil.div(used, total, 4), 100));
            sysFiles.add(sysFile);
        }
        return sysFiles;
    }

    private static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }
}
