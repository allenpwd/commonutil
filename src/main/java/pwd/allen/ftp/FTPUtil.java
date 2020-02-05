package pwd.allen.ftp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 依赖包：commons-net
 *
 * @author 门那粒沙
 * @create 2020-02-05 22:43
 **/
public class FTPUtil {

    //ftp服务器地址
    private String hostname;
    //ftp服务器端口号默认为21
    private Integer port;
    //ftp登录账号
    private String username;
    //ftp登录密码
    private String password;

    public FTPUtil(String hostname, Integer port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    private FTPClient ftpClient = null;

    /**
     * 初始化ftp服务器
     */
    public void init() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            System.out.println("connecting...ftp服务器:" + hostname + ":" + port);
            ftpClient.connect(hostname, port); //连接ftp服务器
            ftpClient.login(username, password); //登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("connect failed...ftp服务器:" + hostname + ":" + port);
            }
            System.out.println("connect successful...ftp服务器:" + hostname + ":" + port);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return
     */
    public boolean downloadFile(String pathname, String filename, String localpath) {
//        ftpClient.enterLocalPassiveMode();
        boolean flag = false;
        OutputStream os = null;
        if (localpath == null) localpath = "";
        if (!localpath.endsWith(File.separator)) localpath += File.separator;
        try {
            System.out.println("开始下载文件");
            //切换FTP目录
            if (StringUtils.isNotEmpty(pathname)) {
                ftpClient.changeWorkingDirectory(pathname);
            }
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + file.getName());
                    os = new FileOutputStream(localFile);
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    flag = ftpClient.retrieveFile(file.getName(), os);
                    if (!flag) {
                        System.out.println("下载文件失败");
                    } else {
                        System.out.println("下载文件成功");
                    }
                    os.close();
                }
            }
        } catch (Exception e) {
            System.out.println("下载文件失败" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 删除文件 *
     *
     * @param filename 要删除的文件名称 *
     * @return
     */
    public boolean deleteFile(String filename, String pathname) {
        ftpClient.enterLocalPassiveMode();
        boolean flag = false;
        try {
            ftpClient.changeWorkingDirectory(pathname);
            System.out.println("开始删除文件");
            flag = ftpClient.deleteFile(pathname + filename);
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        } finally {
        }
        return flag;
    }


    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    public void list(String pathName, List<String> arFiles) throws IOException {
        if (arFiles == null) arFiles = new ArrayList<>();
        ftpClient.enterLocalPassiveMode();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            String directory = pathName;
            //更换目录到当前目录
            this.ftpClient.changeWorkingDirectory(directory);
            FTPFile[] files = this.ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    arFiles.add(directory + file.getName());
                } else if (file.isDirectory()) {
                    list(directory + file.getName() + "/", arFiles);
                }
            }
        }
    }

    /**
     * 递归遍历目录下面指定的文件名
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @param ext      文件的扩展名
     * @throws IOException
     */
    public void list(String pathName, String ext, List<String> arFiles) throws IOException {
        ftpClient.enterLocalPassiveMode();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            String directory = pathName;
            //更换目录到当前目录
            this.ftpClient.changeWorkingDirectory(directory);
            FTPFile[] files = this.ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(ext)) {
                        arFiles.add(file.getName());
                    }
                } else if (file.isDirectory()) {
                    list(directory + file.getName() + "/", ext, arFiles);
                }
            }
        }
    }

    public void destroy() {
        try {
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
