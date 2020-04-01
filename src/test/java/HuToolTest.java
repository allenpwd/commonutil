import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.clone.Cloneable;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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
    }



    @Data
    @AllArgsConstructor
    public class MyObj extends CloneSupport<MyObj> {
        private int num;
        private String str;
        private Date date;
        private float f;
    }
}
