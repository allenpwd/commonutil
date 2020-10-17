package pwd.allen.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author 门那粒沙
 * @create 2020-10-17 9:17
 **/
public class FTPTest {

    private String hostname;
    private Integer port;
    private String username;
    private String password;

    @Before
    public void init() throws IOException {
        Properties properties = new Properties();
        properties.load(FTPTest.class.getClassLoader().getResourceAsStream("ftp.properties"));
        hostname = properties.getProperty("hostname");
        port = Integer.parseInt(properties.getProperty("port"));
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    @Test
    public void my() throws IOException {
        FTPUtil ftpUtil = new FTPUtil(hostname, port, username, password);
        ftpUtil.init();
        ArrayList<String> list = new ArrayList<>();
        ftpUtil.list("/home/", list);
        System.out.println(list);
    }

    /**
     * 使用hutool的ftp工具
     */
    @Test
    public void hutool() throws IOException {
        Ftp ftp = new Ftp(hostname, port, username, password, Charset.defaultCharset(), FtpMode.Passive);

        System.out.println("当前路径：" + ftp.pwd());

        ftp.cd("/home");

        //当前目录下的内容
        List<String> stringList = ftp.ls("");
        System.out.println(stringList);
        FTPFile[] ftpFiles = ftp.lsFiles("");
        for (FTPFile ftpFile : ftpFiles) {
            System.out.println(ftpFile.getName() + "---------" + ftpFile.getRawListing());
        }

        // 创建目录，不支持多级目录创建，即上级目录一定要存在
        if (!ftp.exist("ftp/dir/abc")) {
            System.out.println("创建目录：" + ftp.mkdir("ftp/dir/abc"));
        }
        // 支持多级创建，不过路径需要是绝对路径，默认会从根路径开始创建
        ftp.mkDirs("home/ftp/dir/abc");

        ftp.cd("./ftp");

        // 上传文件
        System.out.println("上传文件：" + ftp.upload("dir", "ftp.properties", FileUtil.file("ftp.properties")));

        // 下载文件
        ftp.download("dir/ftp.properties", new File(System.getProperty("user.home") + "\\Desktop\\"));

        System.out.println("删除文件：" + ftp.delFile("dir/ftp.properties"));
        System.out.println("删除目录：" + ftp.delDir("dir/abc"));

        ftp.close();

    }
}
