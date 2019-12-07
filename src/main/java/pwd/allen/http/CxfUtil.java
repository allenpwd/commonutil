package pwd.allen.http;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 门那粒沙
 * @create 2019-08-18 17:24
 **/
public class CxfUtil {

    public static String get(String url) throws IOException {
        WebClient webClient = WebClient.create(url);

        Response response = webClient.get();

        InputStream ips = (InputStream) response.getEntity();

        return IOUtils.readStringFromStream(ips);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(get("http://www.baidu.com"));
    }

}
