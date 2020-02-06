package pwd.allen.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.*;

/**
 * @author 门那粒沙
 * @create 2020-02-05 22:51
 **/
public class FTPUtilTest {

    private FTPUtil ftpUtil;

    @Before
    public void setUp() throws Exception {
        ftpUtil = new FTPUtil("192.168.118.102", 21, "ftptest", "123");
        ftpUtil.init();
    }

    @Test
    public void downloadFile() {
        String localPath = "C:\\Users\\Administrator\\Desktop";
        System.out.println(ftpUtil.downloadFile("", "abc.txt", localPath));
        System.out.println(ftpUtil.downloadFile("", "abc.doc", localPath));
        System.out.println(ftpUtil.downloadFile("", "guns.pdf", localPath));
    }

    @Test
    public void test() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            ftpClient.connect("192.168.118.102", 21); //连接ftp服务器
            ftpClient.login("ftptest", "123"); //登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("connect failed...ftp服务器:");
            }
            System.out.println("connect successful...ftp服务器:");

            System.out.println(ftpClient.pwd());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}