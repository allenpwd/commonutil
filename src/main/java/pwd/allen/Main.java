package pwd.allen;

import cn.hutool.core.util.RuntimeUtil;
import pwd.allen.convert.EncryptUtil;

/**
 * @author 门那粒沙
 * @create 2021-07-17 16:02
 **/
public class Main {

    public static void main(String[] args) {
        String src = "base64双向加密";
        String encStr = EncryptUtil.base64Enc(src);
        System.out.println(encStr);
        System.out.println(EncryptUtil.base64Dec(encStr));

        System.out.println(RuntimeUtil.execForStr("ipconfig"));
    }
}
