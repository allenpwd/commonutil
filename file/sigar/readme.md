sigar中有很多的实现是通过jni实现的，所以运行在不同的平台上需要加载对应的本地动态链接库
sigar.jar的底层是用C语言编写的，它通过本地方法来调用操作系统API来获取系统相关数据

依赖
- Windows：sigar-amd64-winnt.dll或sigar-x86-winnt.dll
- linux：libsigar-amd64-linux.so或libsigar-x86-linux.so