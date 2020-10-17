package pwd.allen.script;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RuntimeUtil;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author 门那粒沙
 * @create 2020-10-17 17:55
 **/
public class ShellUtilsTest {

    private String command = "cmd /c run\\start.bat";
//    private String command = "sh run/start.sh &";


    @Test
    public void my() {
        System.out.println("--------------ipconfig----------------");
        System.out.println(Arrays.toString(ShellUtils.exec("ipconfig", "GBK")));

        System.out.println("--------------java jar----------------");
        System.out.println(Arrays.toString(ShellUtils.exec(command, "GBK")));
    }

    @Test
    public void hutool() throws InterruptedException {
        System.out.println("--------------ipconfig----------------");
        System.out.println(RuntimeUtil.execForStr("ipconfig"));

        System.out.println("--------------netstat----------------");
        Process exec = RuntimeUtil.exec("netstat -ano");
        System.out.println(IoUtil.read(exec.getInputStream(), Charset.forName("GBK")));
        exec.destroy();

        System.out.println("--------------java jar----------------");
        RuntimeUtil.execForStr(Charset.forName("GBK"), command);

        System.out.println("--------------error----------------");
        RuntimeUtil.execForStr(Charset.forName("GBK"), "nets");
    }

}