package pwd.allen.script;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author lenovo
 * @create 2019-12-26 11:15
 **/
public class ShellUtils {

    public static void main(String[] args) {
        String command = "cmd /c run\\start.bat";
        System.out.println(Arrays.toString(exec(command, "GBK")));

//        command = "sh run/start.sh &";
//        System.out.println(Arrays.toString(exec(command, "GBK")));
    }

    public static String[] exec(String command, String charset) {
        String str_info = null;
        String str_error = null;
        Process process = null;

        try {
            if (StringUtils.isEmpty(charset)) charset = "utf-8";
            process = Runtime.getRuntime().exec(command);

//            str_info = IOUtils.toString(process.getInputStream(), charset);

            //获取响应流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }


            int exitValue = process.waitFor();

            if (exitValue != 0) {
                System.err.println("error");
//            str_error = IOUtils.toString(process.getErrorStream(), charset);

                //获取错误流
                bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));
                line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{str_info, str_error};
    }
}
