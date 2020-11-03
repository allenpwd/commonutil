package pwd.allen.convert;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 门那粒沙
 * @create 2019-07-24 22:48
 **/
public class XMLTest {

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
        Calendar c = Calendar.getInstance();
        System.out.println(c.getTime().getHours());

        System.out.println(new SimpleDateFormat("yyyyMMdd0SSS").format(new Date()));
    }

    /**
     * java saxbuilder 读取xml
     * @throws JDOMException
     * @throws IOException
     */
    @Test
    public void test2() throws JDOMException, IOException {
        SAXBuilder sb = new SAXBuilder();

        Document doc = sb.build(XMLTest.class.getClassLoader().getResourceAsStream("xml/test.xml")); //构造文档对象
        Element root = doc.getRootElement(); //获取根元素HD
        List list = root.getChildren("bean");//取名字为disk的所有元素
        for (int i = 0; i < list.size(); i++) {
            Element element = (Element) list.get(i);
            String id = element.getAttributeValue("id");
            String clazz = element.getAttributeValue("class");
            String name = element.getChildText("name");//取disk子元素capacity的内容
            System.out.println(id);
            System.out.println(clazz);
            System.out.println(name);
        }
    }
}
