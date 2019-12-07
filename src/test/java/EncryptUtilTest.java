import org.junit.Test;

/**
 * @author lenovo
 * @create 2019-08-29 16:24
 **/
public class EncryptUtilTest {

    @Test
    public void testBase64() {
        String src = "base64双向加密";
        String encStr = EncryptUtil.base64Enc(src);
        System.out.println(encStr);
        System.out.println(EncryptUtil.base64Dec(encStr));
    }

    @Test
    public void testMD5() {
        String src = "md5单向加密";
        String encStr = EncryptUtil.MD5Encode(src);
        System.out.println(encStr);
    }

    @Test
    public void test() {
        String str = "1a\nasdf";
        System.out.println(EncryptUtil.generateDESKey(str));

    }

}