package pwd.allen.file;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * @author 门那粒沙
 * @create 2019-12-07 21:12
 **/
public class FileUtil {


    public static int getFileLineNum(String filePath) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))){
            lineNumberReader.skip(Long.MAX_VALUE);
            int lineNumber = lineNumberReader.getLineNumber();
            return lineNumber + 1;//实际上是读取换行符数量 , 所以需要+1
        } catch (IOException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        System.out.println(getFileLineNum("C:\\Users\\Administrator\\Desktop\\201910收入数据清单1125.csv"));
        System.out.println(System.currentTimeMillis() - begin);
    }

}
