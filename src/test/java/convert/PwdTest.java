package convert;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 门那粒沙
 * @create 2019-07-24 22:48
 **/
public class PwdTest {

    @Test
    public void testXML() {
        ArrayList<Object> list = new ArrayList<>();
        list.add("String");
        list.add(100);
        list.add(0.123);
        list.add(false);

        Map<String, Object> map = new HashMap<>();
        map.put("string", "String");
        map.put("int", 100);
        map.put("float", 0.123);
        map.put("boolean", true);
        map.put("list", list);
    }

    @Test
    public void test() {
        File file = new File("C:\\Users\\Administrator\\Desktop\\abc\\git\\.git");
        System.out.println(file.mkdirs());
    }
}
