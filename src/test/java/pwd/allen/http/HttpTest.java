package pwd.allen.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpUtil;
import org.junit.Test;

/**
 * @author 门那粒沙
 * @create 2020-10-17 16:01
 **/
public class HttpTest {

    @Test
    public void hutool() {

        // get请求
        String url = "http://api.qingyunke.com/api.php?key=free";
        String body = HttpUtil.createGet(url)
                .header("my", "my header")
                .form("msg", "鹅鹅鹅")
                .form("appid", 0)
                .execute().body();
        System.out.println(body);

        // 上传文件
        url = "https://httpbin.org/post";
        body = HttpUtil.createPost(url)
                .header("origin", "https://www.layui.com")
                .header("referer", "https://www.layui.com/demo/upload.html")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "cross-site")
                .form("file", FileUtil.file("img/img.jpg"))
                .execute().body();
        System.out.println(body);
    }
}
