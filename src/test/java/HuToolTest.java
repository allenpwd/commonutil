import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ICaptcha;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 门那粒沙
 * @create 2020-03-23 23:11
 **/
public class HuToolTest {

    @Test
    public void test() throws IOException {
        ICaptcha captcha = CaptchaUtil.createCircleCaptcha(500, 300);
        captcha = CaptchaUtil.createShearCaptcha(500, 300);
        FileOutputStream outputStream = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\test.jpg"));
        captcha.write(outputStream);
    }
}
