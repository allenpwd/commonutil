import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.system.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 官方文档：https://hutool.cn/docs
 * @author 门那粒沙
 * @create 2020-03-23 23:11
 **/
public class HuToolTest implements Serializable {

    /**
     * 测试下生成图形验证码
     * @throws IOException
     */
    @Test
    public void captcha() throws IOException {
        ICaptcha captcha = CaptchaUtil.createCircleCaptcha(500, 300);
        //扭曲干扰的验证码
        captcha = CaptchaUtil.createShearCaptcha(500, 300, 6, 8);
        captcha = CaptchaUtil.createLineCaptcha(500, 300, 6, 10000);

        //输出成图片文件
        FileOutputStream outputStream = new FileOutputStream(new File("C:\\Users\\\\lenovo\\Desktop\\test.jpg"));
        captcha.write(outputStream);

        System.out.println(captcha.getCode());
        //校验验证码
        System.out.println(captcha.verify("12345"));
    }

    /**
     * 对象克隆
     * 不懂：MyObj不用实现Serialable，而是父类去实现这个接口
     */
    @Test
    public void cloneable() {
        //浅拷贝
        MyObj myObj = new MyObj(12, "测试", new Date(), 0.12f);
        MyObj clone = myObj.clone();
        System.out.println(clone);
        System.out.println(clone == myObj);

        //深拷贝，需要支持序列化，否则返回null
        MyObj clone1 = ObjectUtil.cloneByStream(myObj);
        System.out.println(clone1);
        System.out.println(myObj == clone1);
    }
    @Data
    @AllArgsConstructor
    public class MyObj extends CloneSupport<MyObj> {
        private int num;
        private String str;
        private Date date;
        private float f;
    }

    /**
     * 复制属性
     */
    @Test
    public void copyProperty() {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("num", 12);
        paramMap.put("str", null);
        paramMap.put("date", DateUtil.parse("2020-12-27", "yyyy-MM-dd"));
        paramMap.put("f", "0.12");

        MyObj obj = new MyObj(1, "str", new Date(), 0.1f);
        System.out.println(obj);
        BeanUtil.copyProperties(paramMap, obj, CopyOptions.create()
                .setIgnoreNullValue(true));
        System.out.println(obj);
    }


    @Test
    public void convert() {
        System.out.println(Convert.toFloat("12.3"));
        System.out.println(Convert.toStr(new String[]{"a", "b", "c"}));
        System.out.println(Convert.toStr(Collections.singletonMap("name", "test")));
        System.out.println(Convert.toStr(Arrays.asList("a", "b", "c")));

        //<editor-fold desc="半角 全角转换">
        //半角转全角
        String sbc = Convert.toSBC("1a.,A你。");
        System.out.println(sbc);
        //全角转半角
        System.out.println(Convert.toDBC(sbc));
        //</editor-fold>

        //转成16进制字符串
        String str_hex = Convert.toHex("1a哈", CharsetUtil.CHARSET_UTF_8);
        System.out.println(str_hex);
        System.out.println(Convert.hexToStr(str_hex, CharsetUtil.CHARSET_UTF_8));

        //Unicode
        String str_unicode = Convert.strToUnicode("1a哈");
        System.out.println(str_unicode);
        System.out.println(Convert.unicodeToStr(str_unicode));

        //原始类和包装类转换
        System.out.println(Convert.wrap(int.class));
        System.out.println(Convert.unWrap(Integer.class));

        //自定义类型转换器
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        converterRegistry.putCustom(String.class, (value, defaultValue) -> "custom" + value + defaultValue);
        String convert = converterRegistry.convert(String.class, 12);
        System.out.println(convert);
    }

    /**
     * 剪切板
     */
    @Test
    public void clipBoard() {
        //复制文本到剪切板
        ClipboardUtil.setStr("我是文本内容");
        System.out.println(ClipboardUtil.getStr());

        //将图片放到剪切板
        ClipboardUtil.setImage(CaptchaUtil.createCircleCaptcha(500, 300).createImage("沙雕up233"));
    }

    /**
     * 文件监听，基于jdk7的WatchService
     * @throws InterruptedException
     */
    @Test
    public void fileMonitor() throws InterruptedException {
        File file = new File("C:\\Users\\lenovo\\Desktop\\200327");
        //监听文件或目录的所有事件
        WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.EVENTS_ALL);

        //实现一个监听器
        Watcher watcher = new Watcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("创建：{}-> {}", currentPath, obj);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("修改：{}-> {}", currentPath, obj);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("删除：{}-> {}", currentPath, obj);
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("Overflow：{}-> {}", currentPath, obj);
            }
        };

        //注册监听器，并加上延迟，此类通过维护一个Set将短时间内相同文件多次modify的事件合并处理触发
        watchMonitor.setWatcher(new DelayWatcher(watcher, 500));

        //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
        watchMonitor.setMaxDepth(3);
        //启动监听
        watchMonitor.start();

        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void runtime() {
        System.out.println("--------------ipconfig----------------");
        System.out.println(RuntimeUtil.execForStr("ipconfig"));

        System.out.println("--------------netstat----------------");
        Process exec = RuntimeUtil.exec("netstat -ano");
        System.out.println(IoUtil.read(exec.getInputStream()));
    }

    /**
     * 系统属性工具
     */
    @Test
    public void system() {
        System.out.println("--------Java Virtual Machine Specification信息---------");
        System.out.println(SystemUtil.getJvmSpecInfo());
        System.out.println("--------系统信息信息---------");
        System.out.println(SystemUtil.getOsInfo());
        System.out.println("--------Java运行时信息---------");
        System.out.println(SystemUtil.getRuntimeInfo());
    }

    /**
     * 灵活的KV结构
     */
    @Test
    public void dict() {
        Dict dict = new Dict();
        dict.put("int", 123);
        dict.put("float", 12.3f);
        dict.put("string", "hello");

        System.out.println(dict.getFloat("float"));
        System.out.println(dict.getInt("int"));
    }

    @Test
    public void validate() {
        System.out.println("校验身份证：" + Validator.isCitizenId("440607199101011234"));
        Validator.validateChinese("this is China！", "检验到字符串存在中文");
    }


    /**
     * Caused by: com.sun.mail.smtp.SMTPSendFailedException: 554 DT:SPM 126 smtp1,C8mowAAXHlLAKFRgYqYwPg--.13400S2 1616128194
     * please see http://mail.163.com/help/help_spam_16.htm?ip=61.144.96.109&hostid=smtp1&time=1616128194
     */
    @Test
    public void mail() {
        MailAccount account = new MailAccount();
        // 邮件服务器的SMTP地址，可选，默认为smtp.<发件人邮箱后缀>
        account.setHost("smtp.126.com");
        // 邮件服务器的SMTP端口，可选，默认25
        account.setPort(465);
        // 发件人（好像必须是发件人邮箱前缀，否则发送失败）
        account.setFrom("panweidan@126.com");
        // 用户名，默认为发件人邮箱前缀
        account.setUser("panweidan");
        // 密码（注意，某些邮箱需要为SMTP服务单独设置授权码，详情查看相关帮助）
        account.setPass("YCLWHETVOOGDKFXU");
        account.setAuth(true);
        account.setSslEnable(true);
        // 使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展
        account.setStarttlsEnable(true);
        // 指定实现javax.net.SocketFactory接口的类的名称,这个类将被用于创建SMTP的套接字
        account.setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
        account.setTimeout(3000);

        String messageId = MailUtil.send(account, "994266136@qq.com", "测试", "邮件来自Hutool测试", false);
        System.out.println(messageId);
    }

    @Test
    public void map() {
        MapBuilder<Object, Object> builder = MapUtil.builder();
    }

    /**
     * 以文件形式处理模版
     * 会根据引入的jar自动识别模板引擎
     */
    public void template() {
        List<String> list = Arrays.asList("abc", "efg");
        Template template = TemplateUtil
                .createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH))
                .getTemplate("template.xml");
        String result = template.render(MapUtil.builder("list", list).build());
        System.out.println(result);
    }

}
