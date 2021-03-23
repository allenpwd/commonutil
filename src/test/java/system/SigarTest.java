package system;

import org.hyperic.sigar.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * sigar.jar的底层是用C语言编写的，它通过本地方法来调用操作系统API来获取系统相关数据
 * Windows操作系统下Sigar.jar 依赖sigar-amd64-winnt.dll或sigar-x86-winnt.dll
 * linux 操作系统下则依赖libsigar-amd64-linux.so或libsigar-x86-linux.so
 *
 * @author pwdan
 * @create 2021-03-23 10:23
 **/
public class SigarTest {

    private Sigar sigar = new Sigar();

    @Test
    public void test() {
        //获得系统属性集
        Properties props = System.getProperties();

        // 将依赖库放到这个目录下
        System.out.println("lib path:" + props.getProperty("java.library.path"));
        //操作系统名称
        String osName = props.getProperty("os.name");
        System.out.println("操作系统名称:" + osName);
        //操作系统构架
        String osArch = props.getProperty("os.arch");
        System.out.println("系统架构:" + osArch);
        //操作系统版本
        String osVersion = props.getProperty("os.version");
        System.out.println("操作系统版本:" + osVersion);
        //Java安装目录
        String home = props.getProperty("java.home");
        System.out.println("java安装目录:" + home);
        //用户的账户名称
        String user = props.getProperty("user.home");
        System.out.println("用户帐户名称:" + user);
        //Java 运行时环境规范名称
        String version = props.getProperty("java.specification.name");
        System.out.println("Java 运行时环境规范名称 :" + version);
        //获取当前jdk的版本号
        String number = props.getProperty("java.specification.version");
        System.out.println("jdk的版本号:" + number);
    }

    @Test
    public void os() {
        OperatingSystem OS = OperatingSystem.getInstance();
        System.out.println("=========操作系统========");
        // 操作系统内核类型如： 386、486、586等x86
        System.out.println("操作系统内核 = " + OS.getArch());
        System.out.println("OS.getCpuEndian() = " + OS.getCpuEndian());
        System.out.println("OS.getDataModel() = " + OS.getDataModel());
        System.out.println("系统描述 = " + OS.getDescription());
        System.out.println("系统类型 = " + OS.getName());
        System.out.println("系统卖主 = " + OS.getVendor());
        System.out.println("卖主名称 = " + OS.getVendorCodeName());
        System.out.println("系统名称 = " + OS.getVendorName());
        System.out.println("系统类型 = " + OS.getVendorVersion());
        System.out.println("版本号 = " + OS.getVersion());
    }

    @Test
    public void cpu() throws SigarException, UnknownHostException {
        System.out.println("==============cpu===================");
        CpuInfo infos[] = sigar.getCpuInfoList();
        CpuPerc cpu = sigar.getCpuPerc();
        String address = InetAddress.getLocalHost().getHostAddress();
        System.out.println("本机IP地址" + address);
        for (int i = 0; i < infos.length; i++) {
            CpuInfo info = infos[i];
            //CPU的总量MHz
            System.out.println("CPU总量mhz=" + info.getMhz());
            //获得CPU的卖主，如：Intel
            System.out.println("vendor=" + info.getVendor());
            //获得CPU的类别，如：Celeron
            System.out.println("model=" + info.getModel());
            //缓冲存储器数量
            System.out.println("cache size=" + info.getCacheSize());
            // 用户使用率
            System.out.println("User :" + CpuPerc.format(cpu.getUser()));
            // 系统使用率
            System.out.println("Sys :" + CpuPerc.format(cpu.getSys()));
        }
    }


    @Test
    public void mem() throws SigarException {
        // 物理内存信息
        Mem mem = sigar.getMem();
        // 内存总量
        System.out.println("Total = " + mem.getTotal() / 1024L / 1024 + "M av");
        // 当前内存使用量
        System.out.println("Used = " + mem.getUsed() / 1024L / 1024 + "M used");
        // 当前内存剩余量
        System.out.println("Free = " + mem.getFree() / 1024L / 1024 + "M free");

        // 系统页面文件交换区信息
        Swap swap = sigar.getSwap();
        // 交换区总量
        System.out.println("Total = " + swap.getTotal() / 1024L + "K av");
        // 当前交换区使用量
        System.out.println("Used = " + swap.getUsed() / 1024L + "K used");
        // 当前交换区剩余量
        System.out.println("Free = " + swap.getFree() / 1024L + "K free");
    }

    /**
     * 取当前系统进程表中的用户信息
     * @throws SigarException
     */
    @Test
    public void device() throws SigarException {
        // 取当前系统进程表中的用户信息
        Who who[] = sigar.getWhoList();
        if (who != null && who.length > 0) {
            for (int i = 0; i < who.length; i++) {
                System.out.println("\n~~~~~~~~~" + String.valueOf(i) + "~~~~~~~~~");
                Who _who = who[i];
                System.out.println("getDevice() = " + _who.getDevice());
                System.out.println("getHost() = " + _who.getHost());
                System.out.println("getTime() = " + _who.getTime());
                // 当前系统进程表中的用户名
                System.out.println("getUser() = " + _who.getUser());
            }
        }
    }


    @Test
    public void disk() throws SigarException {
        FileSystem fslist[] = sigar.getFileSystemList();
        System.out.println("长度为什么是:" + fslist.length);

        for (int i = 0; i < fslist.length; i++) {
            System.out.println("============硬盘描述============");
            System.out.println("\n~~~~~~~~~~" + i + "~~~~~~~~~~");
            FileSystem fs = fslist[i];
            // 分区的盘符名称
            System.out.println("fs.getDevName() = " + fs.getDevName());
            // 分区的盘符名称
            System.out.println("fs.getDirName() = " + fs.getDirName());

            // 文件系统类型，比如 FAT32、NTFS
            System.out.println("fs.getSysTypeName() = " + fs.getSysTypeName());
            // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
            System.out.println("fs.getTypeName() = " + fs.getTypeName());
            // 文件系统类型

            FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
            String sub = fs.getDevName().substring(0, 1);
            // 文件系统总大小
            System.out.println(" 硬盘 " + sub + "=" + usage.getTotal() / 1024
                    / 1024 + "GB");

            // 文件系统剩余大小
            System.out.println(" 硬盘剩余大小= " + usage.getFree() / 1024 / 1024
                    + "GB");
            // 文件系统可用大小
            System.out.println(" 硬盘可用大小 = " + usage.getAvail() / 1024 / 1024
                    + "GB");
            // 文件系统已经使用量
            System.out.println(" 经使用量 = " + usage.getUsed() / 1024 / 1024
                    + "GB");
            double usePercent = usage.getUsePercent() * 100D;
            // 文件系统资源的利用率
            System.out.println(" 利用率 = " + usePercent + "%");

            System.out.println(" DiskReads = " + usage.getDiskReads());
            System.out.println(" DiskWrites = " + usage.getDiskWrites());
        }

    }

    @Test
    public void desktop() throws IOException {
        if (java.awt.Desktop.isDesktopSupported()) {
            //创建一个URI实例
            java.net.URI uri = java.net.URI.create("http://cn.bing.com/");
            //获取当前系统桌面扩展
            java.awt.Desktop dp = java.awt.Desktop.getDesktop();
            //判断系统桌面是否支持要执行的功能
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                //获取系统默认浏览器打开链接
                dp.browse(uri);
            }
        }
    }

    @Test
    public void network() throws SigarException {
        // 获取网络流量等信息
        String ifNames[] = sigar.getNetInterfaceList();
        for (int i = 0; i < ifNames.length; i++) {
            String name = ifNames[i];
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            System.out.println("\nname = " + name);// 网络设备名
            System.out.println("Address = " + ifconfig.getAddress());// IP地址
            System.out.println("Netmask = " + ifconfig.getNetmask());// 子网掩码
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                System.out.println("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
            System.out.println("RxPackets = " + ifstat.getRxPackets());// 接收的总包裹数
            System.out.println("TxPackets = " + ifstat.getTxPackets());// 发送的总包裹数
            System.out.println("RxBytes = " + ifstat.getRxBytes());// 接收到的总字节数
            System.out.println("TxBytes = " + ifstat.getTxBytes());// 发送的总字节数
            System.out.println("RxErrors = " + ifstat.getRxErrors());// 接收到的错误包数
            System.out.println("TxErrors = " + ifstat.getTxErrors());// 发送数据包时的错误数
            System.out.println("RxDropped = " + ifstat.getRxDropped());// 接收时丢弃的包数
            System.out.println("TxDropped = " + ifstat.getTxDropped());// 发送时丢弃的包数
        }


        // 一些其他的信息
        for (int i = 0; i < ifNames.length; i++) {
            NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifNames[i]);
            if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress())
                    || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                    || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                continue;
            }
            System.out.println("cfg.getAddress() = " + cfg.getAddress());// IP地址
            System.out.println("cfg.getBroadcast() = " + cfg.getBroadcast());// 网关广播地址
            System.out.println("cfg.getHwaddr() = " + cfg.getHwaddr());// 网卡MAC地址
            System.out.println("cfg.getNetmask() = " + cfg.getNetmask());// 子网掩码
            System.out.println("cfg.getDescription() = " + cfg.getDescription());// 网卡描述信息
            System.out.println("cfg.getType() = " + cfg.getType());//
            System.out.println("cfg.getDestination() = " + cfg.getDestination());
            System.out.println("cfg.getFlags() = " + cfg.getFlags());//
            System.out.println("cfg.getMetric() = " + cfg.getMetric());
            System.out.println("cfg.getMtu() = " + cfg.getMtu());
            System.out.println("cfg.getName() = " + cfg.getName());
        }
    }
}
