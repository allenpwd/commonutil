package convert;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * @author 门那粒沙
 * @create 2019-07-24 21:46
 **/
public class XMLUtil {
    /**
     * 属性标识
     */
    public static final String attributeMark = "@";

    /**
     * 将xml字符串<STRONG>转换</STRONG>为JSON字符串
     *
     * @param xmlString
     *            xml字符串
     * @return JSON<STRONG>对象</STRONG>
     */
    public static String xml2json(String xmlString) {
        XMLSerializer xmlSerializer = new XMLSerializer();

        JSON json = xmlSerializer.read(xmlString);
        return json.toString();
    }

    /**
     * 将xmlDocument<STRONG>转换</STRONG>为JSON<STRONG>对象</STRONG>
     *
     * @param xmlDocument
     *            XML Document
     * @return JSON<STRONG>对象</STRONG>
     */
    public static String xml2json(Document xmlDocument) {
        return xml2json(xmlDocument.toString());
    }

    /**
     * JSON(数组)字符串<STRONG>转换</STRONG>成XML字符串。<br/>
     * json字符串，属性字段转xml时，需要添加@标识。<br/>
     *
     * <pre>
     * 如：
     *	"Application": {
     *		"@decisionNum": "01",
     *		"@applicationID": "adfsdf",
     *		"MessageList": {
     *			"StatusCode": "1",
     *			"StatusDescription": "Successful"
     *		}
     *	}
     * </pre>
     *
     * @param jsonString
     * @return xml 数据
     */
    public static String json2xml(String jsonString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        return xmlSerializer.write(JSONSerializer.toJSON(jsonString));
        // return
        // xmlSerializer.write(JSONArray.fromObject(jsonString));//这种方式只支持JSON数组
    }

    /**
     * 取得xml文件的根节点名称，即消息名称。
     *
     * @param xmlStr
     *            xml内容
     * @return String 返回名称
     */
    public static String getRootName(String xmlStr) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        Element root = doc.getRootElement();
        return root.getName();
    }

    private static void getAttributeValue(Element e, Map<String, Object> map) {
        if (e.getAttributes().size() > 0) {
            Iterator<Attribute> it_attr = e.getAttributes().iterator();
            while (it_attr.hasNext()) {
                Attribute attribute = (Attribute) it_attr.next();
                String attrname = attribute.getName();
                String attrvalue = e.getAttributeValue(attrname);
                map.put(attrname, attrvalue);
            }
        }
    }

    /**
     * 把xml文件转换为map形式，其中key为有值的节点名称
     *
     * @param xmlStr
     *            xml内容
     * @return Map 转换为map返回
     */
    public static Map<String, Object> xml2Map(String xmlStr) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        if (doc == null) {
            return rtnMap;
        }

        // 得到根节点
        Element rootElement = doc.getRootElement();

        Map<String, Object> bodyMap = new HashMap<String, Object>();
        rtnMap.put(rootElement.getName(), bodyMap);

        element2map(rootElement, bodyMap);
        return rtnMap;
    }

    /**
     * 递归函数，找出最下层的节点并加入到map中，由xml2Map方法调用。
     *
     * @param element
     *            xml节点，包括根节点
     * @param map
     *            目标map
     */
    public static void element2map(Element element, Map<String, Object> map) {
        if (null == element) {
            return;
        }
        getAttributeValue(element, map);

        String name = element.getName();
        List<Element> children = element.getChildren();
        if (!"".equals(element.getTextTrim())) {
            map.put(name, element.getText());
        } else if (children != null && children.size() > 0) {
            for (Element elmtSub : children) {
                Map<String, Object> mapSub = new HashMap<String, Object>();
                String childName = elmtSub.getName();
                element2map(elmtSub, mapSub);

                Object first = map.get(childName);
                if (null == first) {
                    map.put(childName, mapSub);
                } else {
                    if (first instanceof List<?>) {
                        ((List) first).add(mapSub);
                    } else {
                        List<Object> listSub = new ArrayList<Object>();
                        listSub.add(first);
                        listSub.add(mapSub);
                        map.put(childName, listSub);
                    }
                }
            }

        }
    }

    /**
     * 格式化xml
     *
     * @param xmlStr
     *
     * @return String
     * @throws Exception
     */
    public static String formatXml(String xmlStr) throws Exception {
        return formatXml(xmlStr, "utf-8");
    }

    /**
     * 格式化xml
     *
     * @param xmlStr
     * @param encoding
     *
     * @return String
     * @throws Exception
     */
    public static String formatXml(String xmlStr, String encoding) throws Exception {
        SAXBuilder sb = new SAXBuilder();
        Reader reader = new StringReader(xmlStr);

        Document doc = null;
        try {
            doc = sb.build(reader);
        } catch (Exception e) {
            throw e;
        }

        Format prettyFormat = Format.getPrettyFormat();
        prettyFormat.setEncoding(encoding);
        XMLOutputter outputter = new XMLOutputter(prettyFormat);
        String result = "";
        if (null != doc) {
            result = outputter.outputString(doc);
        }

        return result;
    }

    /**
     *
     * map2Xml <br/>
     * map对象，字段生成为xml属性时，key需要添加@标识。<br/>
     * 列如map实例对象字符串：{Application={@applicationID=adfsdf, AppliInfo={@age=23,name=yame}}}
     * <br/>
     *
     * @param map
     * @return
     * @throws Exception
     */
    public static String map2Xml(Map map) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        mapToXmlHandle(map, sb);
        try {
            return sb.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    private static void mapToXmlHandle(Map map, StringBuffer sb) {
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value instanceof List) {
                List list = (List) value;

                for (int i = 0; i < list.size(); i++) {
                    sb.append("<");
                    sb.append(key);
                    sb.append(">");
                    Map hm = (Map) list.get(i);
                    mapToXmlHandle(hm, sb);
                    sb.append("</");
                    sb.append(key);
                    sb.append(">");
                }

            } else {
                if (value instanceof Map) {
                    sb.append("<");
                    sb.append(key);
                    sb.append(">");
                    mapToXmlHandle((HashMap) value, sb);
                    sb.append("</");
                    sb.append(key);
                    sb.append(">");
                } else {
                    if (key.startsWith(attributeMark)) {
                        String tempAtt = " " + key.replaceAll(attributeMark, "") + "=\"" + value + "\"";
                        sb.insert(sb.length() - 1, tempAtt);
                    } else {
                        sb.append("<" + key + ">" + value + "</" + key + ">");
                    }
                }

            }

        }
    }
}
