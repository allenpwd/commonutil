package pwd.allen.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 门那粒沙
 * @create 2020-10-17 16:01
 **/
public class HttpTest {

    private static final Logger logger = LoggerFactory.getLogger(HttpTest.class);

    /**
     * 简易Http服务器
     */
    @Test
    public void server() throws InterruptedException {
        HttpUtil.createServer(8888)
                .setRoot(System.getProperty("user.home") + "\\Desktop\\")
                // 返回JSON数据测试
//                .addAction("/restTest", (request, response) ->
//                        response.write("{\"id\": 1, \"msg\": \"OK\"}", ContentType.JSON.toString())
//                )
                .start();
        while (Thread.activeCount() > 2) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

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

    @Test
    public void xiaomi() throws InterruptedException {

        String[] contents = {"为何偏偏选择最远的地方发货", "我在广州啊干嘛从北京发货", "干嘛不从月球发货", "干嘛不从高风险地区发货", "想害死我吗"
                , "我几号下的单你自己看看", "多少天了", "4天了才给我发货"
                , "发热这么严重", "耗电这么快", "一个早上一半的电没了", "正常使用一天得三充", "安兔兔跑个分别人降4%的电，我降8%的电", "安兔兔压力测试直接超过45度自动退出"
                , "米11火龙当之无愧", "问题集中机", "这手机相当于养了台电老鼠", "功能没用上，忙着充电了一天天的", "120HZ，2k都没开就这么耗电了"
                , "手机发热到烫手", "我花钱买罪受吗", "已经知道这机子耗电快发热也明显，但没想到这么夸张，比网上说的夸张多了，故意给我发了台问题机"
                , "论坛上都在说的问题，没想当我拿到的这台更加严重，而且不是一点点", "这台机已达到无法忍受的地步", "基本上2、3分钟一格电", "掉电离谱得很"
                , "别和我说换货", "简单一句换货就行了，我花钱买罪受吗", "当我傻的吗", "我不能白白受这委屈"
                , "联系小米热线很多次，都没人接", "找了好多渠道反映问题，不是踢皮球就是没人接，只能来找在线客服", "电话专员屁用没有，什么都是一问三不知"
                , "亏我还买了小米家好多东西", "给客户这样一台问题机", "别以为一句退货就完事了，给我造成这么大困扰，一句你可以退货就打发走？"
                , "赔偿", "不是说3天内发货的吗,你们没履行承诺，赔偿", "赶紧给我赔钱", "你们必须给我补偿"
                , "故意的吗", "到底是怎么回事", "我做错了什么要这样对我", "你只会说这几句吗"
                , "我买的是什么颜色的", "我买的是多少存储内存的，帮我查下", "怎么申请退货"
                , "哑巴吗", "说话啊", "会不会说话", "你是哑巴吗", "怎么回复这么慢啊", "故意不回复的吗", "不理人是吧", "赶紧给我答复", "好啊，你敢不鸟我", "拿我当空气吗"
                , "怎么办事的", "就你家特殊啊", "你家货还要隔离7天还是咋地", "我要投诉你", "我曰你仙人"
                , "脑子灌翔了吗", "脑子除了装翔还能装什么", "傻狗", "废物", "垃圾", "脑子坏掉了", "小米是垃圾", "垃圾小米", "小米赶紧倒闭吧"
                , "臭煞笔", "妈了个巴子", "去你吗的", "go fuck yourself", "bitch", "mother fucker", "son of bitch", "you are a shit"
                , "你废不废啊", "活该被人喷", "顶你个肺", "你个扑街", "你是猪吗", "狗都比你会吠", "猪都比你聪明", "你猪狗不如", "你是狗杂种", "畜生", "照照镜子吧猪", "赶紧吠两下给我听"
        };

        Random random = new Random();

        int error = 0;

        while (error < 5) {
            try {
                String body = HttpRequest.post("https://chat.kefu.mi.com/mobile-api/chat/send")
                        .header("Content-Type", "application/json")
                        .header("cookie", "xmuuid=XMGUEST-36357BF0-7954-11EA-8B28-8B20407E558E; mstuid=1586321318535_1413; __utma=127562001.1264774717.1597046174.1597046174.1597046174.1; __utmc=127562001; __utmz=127562001.1597046174.1.1.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; XM_agreement_sure=1; xm_user_www_num=0; XM_79638238_UN=%E9%97%A8%E9%82%A3%E7%B2%92%E6%B2%99; XM_63637688_UN=%E5%BE%8C%E7%94%9F%E4%BA%BA%E6%BB%A8; XM_agreement=0; euid=pgVeoMkeSrB3ZkMuplyFkw%3D%3D; serviceToken=bi+SA53xLh8s1zXoWEDXOAI6rAHbgfR797vbpwxgkPYcPc3x5jexRmt3Ls2B5cPDPCAa8aOJ5Dcsc5O/ataLzOjAMPYuAcm6Thkzj2WPm8sv6qxyhpI103w+PQTzfPtf9FfirmjZLN7bg+QWS2DDFBXAG8PENwOq2Rhq/t7bNiWjK6A2RvcI6ZkfhoztH/i0notFq7BU+oMpsCTimRPOhC+R2XtMpbfrUy7daezEaFnGDOZ0ELcsHmrpzb4dgw4fYC/biyNHhZk8ynlDx46j9F22oj0xsGPxOwch1rsGkLY=; kfs_chat_slh=pljj0kkrQ4Wdhe6UjpXXvq5f4o8=; kfs_chat_ph=G0sfgjtnngFsQpiZ1e37HA==; lastsource=www.baidu.com; userId=79638238; cUserId=54pptC2W2SzEyXJiE0tggegxSbs; axmuid=Pty9e7AQfP9mE%2FKEP%2F4KfbSKLaiTs5XWHSExy4wd0KE%3D; mishopServiceToken=Ihgw4geMP2%2FKN03JXkeq9Eft9iraDbLyGpgxoU8ER%2BSYabPq6JMBUSm%2BFFQ%2FCOE1wCYJEwICUPge1D9Fy75pFH%2FM4OPhwNQcenQHWuWeT1R9IKEK4H4WDhr63yTtkM3OyIVZcBozBk2I%2B9F2MJjEHzoSYdo7WzLIcERIwICJVtc%3D; neoTransfer=1; xm_order_btauth=4918466c1bb48588967ddf893819dea9; xm_link_history=PPPn9MnbbIyt9h6dKDwq5wsZLvQAyTRds6ItOo+Cm4P7XPeicfoq5dskmW8oRqQQ; mUserId=1DtGMwN9rgCBzBQ1%2FbM3yssnB2eNIJb%2B9bAdPJkSqO8%3D; Hm_lvt_c3e3e8b3ea48955284516b186acf0f4e=1609808641,1611888471; Hm_lpvt_c3e3e8b3ea48955284516b186acf0f4e=1611888471; xm_vistor=1586321318535_1413_1611888468295-1611888471046; mstz=||1446895665.647||https%253A%252F%252Fwww.mi.com%252F|https%25253A%25252F%25252Fwww.mi.com%25252F; pageid=dd0ac57e82f8172f")
                        .header("origin", "https://chat.kefu.mi.com")
                        .header("referer", "https://chat.kefu.mi.com/page/index/v2?tag=cn&token=Y24jMTAwMSNjbi53ZWIubWkuaHR0cHMjZGVmYXVsdCNn")
                        .timeout(30 * 1000)
                        .body(String.format("{\"roomId\":\"cn:1001:79638238@P0\",\"userId\":\"79638238\",\"connectionId\":\"websocket-support-1612192839188\",\"content\":\"%s\",\"msgType\":\"TEXT\",\"role\":1,\"tenantId\":\"cn\",\"umsgId\":\"UM|1612192859942\",\"userTenantId\":\"1001\",\"extraProperty\":\"{}\"}", contents[random.nextInt(contents.length)]))					//里面放的是json串
                        .execute()
                        .body();
                JSON parse = JSONUtil.parse(body);
                if (!"success".equals(parse.getByPath("msg", String.class))) {
                    error++;
                }
                logger.info("【{}接口】参数：{}----------返回结果：{}", "importantJobsUrl", "{}", body);
                Thread.sleep(10000 + random.nextInt(20000));
            } catch (Exception e) {
                error++;
                System.out.println(e.getMessage() + "----------------" + error);
            }
        }
    }
}
