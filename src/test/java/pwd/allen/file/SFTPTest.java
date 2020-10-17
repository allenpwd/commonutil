package pwd.allen.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

/**
 * 需要引入 jsch
 * @author 门那粒沙
 * @create 2020-10-17 19:37
 **/
public class SFTPTest {

    private String hostname;
    private Integer port;
    private String username;
    private String password;

    @Before
    public void init() throws IOException {
        Properties properties = new Properties();
        properties.load(SFTPTest.class.getClassLoader().getResourceAsStream("sftp.properties"));
        hostname = properties.getProperty("hostname");
        port = Integer.parseInt(properties.getProperty("port"));
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    @Test
    public void my() throws Exception {
        SFTPUtil util = new SFTPUtil(hostname, port, username, password);

        util.login();

        System.out.println("home:" + util.sftp.getHome());
        System.out.println("serverVersion:" + util.sftp.getServerVersion());

        util.sftp.cd("/home/config/dubboConfig/");

        File file = new File("C:\\Users\\Administrator\\Desktop\\exportExcel.xls");

//        InputStream is = new FileInputStream(file);
//        util.upload("/home/file","", "test_sftp.xls", is);

        byte[] bs = util.download("/home/config/dubboConfig", "bigdatatwo-dubbo.properties");
        System.out.println(bs.length);


        util.logout();
    }

    /**
     * 使用hutool的sftp工具
     */
    @Test
    public void hutool() throws IOException, JSchException {
//        Session session = JschUtil.getSession(hostname, port, username, password);
//        Sftp sftp = new Sftp(session, Charset.defaultCharset());

        Sftp sftp = new Sftp(hostname, port, username, password, Charset.defaultCharset());

        System.out.println("当前路径：" + sftp.pwd());

        //当前目录下的内容，这里path=""会报错
        List<String> stringList = sftp.ls("./");
        System.out.println(stringList);

        // 创建目录，不支持多级目录创建，即上级目录一定要存在，会报错
//        if (!sftp.exist("sftp/dir/abc")) {
//            System.out.println("创建目录：" + sftp.mkdir("sftp/dir/abc"));
//        }
        // 支持多级创建，支持相对路径
        sftp.mkDirs("sftp/dir/abc");

        sftp.cd("./sftp");

        // 上传文件
        System.out.println("上传文件：" + sftp.upload("dir", FileUtil.file("sftp.properties")));

        // 下载文件
        sftp.download("dir/sftp.properties", new File(System.getProperty("user.home") + "\\Desktop\\"));

        System.out.println("删除文件：" + sftp.delFile("dir/sftp.properties"));
        System.out.println("删除目录：" + sftp.delDir("dir/abc"));

        sftp.close();
    }

}