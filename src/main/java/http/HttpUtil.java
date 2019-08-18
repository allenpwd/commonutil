package http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @create 2019-06-14 9:41
 **/
public class HttpUtil {

    private final static Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public final static String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
    public final static String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public final static String CONTENT_TYPE_XML = "text/xml; charset=utf-8";

    private PoolingHttpClientConnectionManager httpClientConnectionManager = null;

    {
        httpClientConnectionManager = new PoolingHttpClientConnectionManager();
    }

    /**
     * 发送请求
     * @param uri
     * @param mapHeader
     * @param param
     * @param methodName
     * @param contentType
     * @return
     * @throws IOException
     */
    public Map<String, Object> sendRequest(String uri, Map<String, String> mapHeader, Object param, String methodName, String contentType) throws IOException, URISyntaxException {

        Map<String, Object> mapRel = new HashMap<String, Object>();

        CloseableHttpClient client = HttpClientBuilder.create().setConnectionManager(httpClientConnectionManager).build();
        CloseableHttpResponse resp = null;


        try {

            HttpRequestBase httpRequestBase = null;
            if (HttpGet.METHOD_NAME.equals(methodName)) {
                httpRequestBase = new HttpGet(uri);
            } else if (HttpPost.METHOD_NAME.equals(methodName)) {
                httpRequestBase = new HttpPost(uri);
            } else if (HttpPut.METHOD_NAME.equals(methodName)) {
                httpRequestBase = new HttpPut(uri);
            } else if (HttpDelete.METHOD_NAME.equals(methodName)) {
                httpRequestBase = new HttpDelete(uri);
            }
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000)//设置连接超时时间，单位毫秒
                    //.setConnectionRequestTimeout(1000)
                    .setSocketTimeout(30000)//请求获取数据的超时时间，单位毫秒
                    //.setProxy(new HttpHost("127.0.0.1", 8888))
                    .build();
            httpRequestBase.setConfig(requestConfig);

            //设置请求头
            if (mapHeader != null && mapHeader.size() > 0) {
                for (Map.Entry<String, String> entry : mapHeader.entrySet()) {
                    httpRequestBase.setHeader(entry.getKey(), entry.getValue());
                }
            }
            if (contentType != null) {
                httpRequestBase.setHeader("Content-Type", contentType);
            }

            //设置请求参数
            if (httpRequestBase instanceof HttpEntityEnclosingRequestBase && param != null) {
                HttpEntityEnclosingRequestBase base = HttpEntityEnclosingRequestBase.class.cast(httpRequestBase);
                if (param instanceof String) {
                    base.setEntity(new StringEntity((String)param, "utf-8"));
                } else if (CONTENT_TYPE_JSON.equals(contentType)) {
                    base.setEntity(new StringEntity(JSON.toJSONString(param), "utf-8"));
                } else if (CONTENT_TYPE_FORM.equals(contentType) && param instanceof Map) {
                    List<BasicNameValuePair> pair =new ArrayList<BasicNameValuePair>();
                    for (Map.Entry<String,Object> entry : ((Map<String, Object>)param).entrySet()) {
                        Object value = entry.getValue();
                        if (value != null) {
                            pair.add(new BasicNameValuePair(entry.getKey(), value.toString()));
                        }
                    }
                    //放入url编码的表单参数
                    base.setEntity(new UrlEncodedFormEntity(pair, "utf-8"));
                }
            } else {//其他情况参数拼在url后
                if (param instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>)param;
                    URIBuilder uriBuilder = new URIBuilder(uri);
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getValue() != null) {
                            uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
                        }
                    }
                    httpRequestBase.setURI(uriBuilder.build());
                }
            }

            resp = client.execute(httpRequestBase);

            //返回响应头
            mapRel.put("header", resp.getAllHeaders());

            HttpEntity he = resp.getEntity();
            InputStream is = he.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = null;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                builder.append(str);
            }

            mapRel.put("result", builder.toString());

            return mapRel;
        } finally {
            if(null!=resp) {
                try {
                    resp.close();
                } catch (IOException e) {
                    log.error(e.toString());
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        HttpUtil httpUtil = new HttpUtil();

        Map<String, Object> mapRel = null;

        //测试请求json接口
        String url = "http://localhost:8083/json";
        String cont = null;
        Map<String, String> mapParam = new HashMap<>();
        mapParam.put("name", "德莱文");
        mapParam.put("age", "23");
        Map<String, String> mapHeader = new HashMap<>();
        mapHeader.put("Cookie", "token=eyJhbGciOiMDE2ND");
        mapRel = httpUtil.sendRequest(url, mapHeader, mapParam, HttpPost.METHOD_NAME, CONTENT_TYPE_JSON);
        JSONObject jsonObject = JSON.parseObject((String)mapRel.get("result"));
//        System.out.println(XMLMap.get(jsonObject, "type", String.class));
//        System.out.println(XMLMap.get(jsonObject, "data.total", Integer.class));

        //测试请求xml接口
        url = "http://www.webxml.com.cn/WebServices/TranslatorWebService.asmx";
//        XMLMap xmlMap = new XMLMap().setAttr("method", "getEnCnTwoWayTranslator");
//        xmlMap.setChild("body")
//                .setAttr("Word", "你好");
//        cont = XMLMap.loadTemplate("xmlTemplate/test2.xml", xmlMap);
//        mapRel = httpUtil.sendRequest(url, mapHeader, cont, HttpPost.METHOD_NAME, CONTENT_TYPE_XML);
//        Map relMap = XMLUtil2.xmlToMap((String)mapRel.get("result"), true);
//        System.out.println(XMLMap.get(relMap, "Envelope.Body.getEnCnTwoWayTranslatorResponse.getEnCnTwoWayTranslatorResult.string", ArrayList.class));
//
//        //测试请求form接口
//        url = "http://localhost:8083/form";
//        mapRel = httpUtil.sendRequest(url, mapHeader, mapParam, HttpPost.METHOD_NAME, CONTENT_TYPE_FORM);
//        jsonObject = JSON.parseObject((String)mapRel.get("result"));
//        System.out.println(XMLMap.get(jsonObject, "name", String.class));
//        System.out.println(XMLMap.get(jsonObject, "data.total", Integer.class));
    }

}

