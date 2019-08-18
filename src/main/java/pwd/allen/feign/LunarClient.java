package pwd.allen.feign;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author 门那粒沙
 * @create 2019-08-18 17:37
 **/
public interface LunarClient {

    @RequestLine("GET /lunar/json.shtml?date={date}")
    @Headers("user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36")
    public LunarResult getLunar(@Param("date") String date);

    @Data
    public class LunarResult {

        private Integer status;
        private String message;

        private Map data;
    }

    /**
     * 测试调用 https://www.sojson.com/open/api/lunar/json.shtml?date= 接口
     * @param args
     */
    public static void main(String[] args) {
        LunarClient lunarClient = Feign.builder().decoder(new GsonDecoder()).target(LunarClient.class, "https://www.sojson.com/open/api/");

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        LunarClient.LunarResult result = lunarClient.getLunar(date);

        System.out.println(result);
    }
}
