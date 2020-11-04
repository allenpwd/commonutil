package pwd.allen.convert;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
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
        List list = root.getChildren("bean");//取名字为bean的所有元素
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

    /**
     * hutool 读取xml
     * @throws JDOMException
     * @throws IOException
     */
    @Test
    public void hutool() throws JDOMException, IOException {

        String xmlStr = IoUtil.read(XMLTest.class.getClassLoader().getResourceAsStream("xml/test.xml"), "utf-8");

        org.w3c.dom.Document doc = XmlUtil.parseXml(xmlStr); //构造文档对象
        org.w3c.dom.Element root = doc.getDocumentElement();//获取根元素HD

        // document转成字符串
        System.out.println(XmlUtil.toStr(doc));

        //取名字为bean的所有元素
        List<org.w3c.dom.Element> beans = XmlUtil.getElements(root, "bean");
        for (org.w3c.dom.Element element : beans) {
            String id = element.getAttribute("id");
            String clazz = element.getAttribute("class");
            String content = element.getTextContent();//取disk子元素capacity的内容
            System.out.println(id);
            System.out.println(clazz);
            System.out.println(content);
        }

        // 读取name元素内容
        System.out.println(XmlUtil.getByXPath("//bean[1]/name", doc, XPathConstants.STRING));
        System.out.println(XmlUtil.getByXPath("beans/bean[1]/name", doc, XPathConstants.STRING));
        System.out.println(XmlUtil.getByXPath("bean[1]/name", root, XPathConstants.STRING));
        // 读取name元素属性
        System.out.println(XmlUtil.getByXPath("bean[1]/@class", root, XPathConstants.STRING));


        // 转 JSON对象
        JSONObject jsonObject = JSONUtil.parseFromXml(xmlStr);
        System.out.println(jsonObject.getByPath("beans.bean.name"));
    }
}
