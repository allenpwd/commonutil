package pwd.allen.file;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

/**
 * 类说明 sftp工具类
 */
public class SFTPUtil {
    private transient Logger log = LoggerFactory.getLogger(this.getClass());

    public ChannelSftp sftp;

    private Session session;
    /**
     * SFTP 登录用户名
     */
    private String username;
    /**
     * SFTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private String host;
    /**
     * SFTP 端口
     */
    private int port;


    //上传文件测试
    public static void main(String[] args) throws Exception {
        SFTPUtil util = new SFTPUtil("root", "password@123", "192.168.200.42", 22);
        //util = new SFTPUtil("root", "123", "192.168.118.101", 22);

        util.login();

        System.out.println("home:" + util.sftp.getHome());
        System.out.println("serverVersion:" + util.sftp.getServerVersion());

        util.sftp.cd("/home/config/dubboConfig/");

        File file = new File("C:\\Users\\Administrator\\Desktop\\exportExcel.xls");
        InputStream is = new FileInputStream(file);

        //util.upload("/home/file","", "test_sftp.xls", is); 

        byte[] bs = util.download("/home/file/temp", "ff80808166af0c800166af0e9c1d0000.xls");
        System.out.println(bs.length);

        util.logout();
    }

    /**
     * 构造基于密码认证的sftp对象
     */
    public SFTPUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SFTPUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SFTPUtil() {
    }


    /**
     * 连接sftp服务器
     */
    public void login() throws Exception {
        JSch jsch = new JSch();
        if (privateKey != null) {
            jsch.addIdentity(privateKey);// 设置私钥  
        }

        session = jsch.getSession(username, host, port);

        if (password != null) {
            session.setPassword(password);
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);
        session.connect();

        sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        sftp.setFilenameEncoding("UTF-8");
    }

    /**
     * 关闭连接 server
     */
    public void logout() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param directory    上传到该目录
     * @param sftpFileName sftp端文件名
     * @param input        输入流
     */
    public void upload(String directory, String sftpFileName, InputStream input) throws SftpException {
        cdOrMkdirs(directory);
        //上传文件
        sftp.put(input, sftpFileName);
    }


    /**
     * 下载文件。
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException {
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        FileOutputStream fop = new FileOutputStream(new File(saveFile));
        sftp.get(downloadFile, fop);
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     */
    public byte[] download(String directory, String downloadFile) throws SftpException, IOException {
        byte[] fileData = null;
        try {
            if (directory != null && !"".equals(directory)) {
                sftp.cd(directory);
            }
            System.out.println(sftp.pwd());
            InputStream is = sftp.get(downloadFile);
            fileData = IOUtils.toByteArray(is);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IOException("文件路径不存在", e);
        }

        return fileData;
    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        if (directory != null) sftp.cd(directory);
        sftp.rm(deleteFile);
    }

    /**
     * 改变目录，如果没有则创建目录
     * @param directory
     * @throws SftpException
     */
    public void cdOrMkdirs(String directory) throws SftpException {
        if (directory == null) return;
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String[] dirs = directory.split("/");
            if (directory.startsWith("/")) dirs[0] = "/" + dirs[0];
            for (String dir : dirs) {
                if (null == dir || "".equals(dir)) continue;
                try {
                    sftp.cd(dir);
                } catch (SftpException ex) {
                    sftp.mkdir(dir);
                    sftp.cd(dir);
                }
            }
        }
    }


    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     */
    public Vector<ChannelSftp.LsEntry> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }


    /**
     * 移动文件
     * @param srcPath
     * @param srcFileName
     * @param targetPath
     * @param targetFileName
     * @throws IOException
     * @throws SftpException
     */
    public void moveFile(String srcPath, String srcFileName, String targetPath, String targetFileName, byte[] bytes) throws IOException, SftpException {
        if (srcPath != null) sftp.cd(srcPath);
        String oldPath = sftp.pwd();
        if (bytes == null) bytes = download(null, srcFileName);
        upload(targetPath, targetFileName, new ByteArrayInputStream(bytes, 0, bytes.length));
        delete(oldPath, srcFileName);
    }

}