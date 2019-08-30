import java.util.Base64;

/**
 * @author lenovo
 * @create 2019-08-30 9:21
 **/
public class TranscodeUtil {

    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 字节数组转hex字符串
     * @param bytes
     * @return
     */
    public static String byteArrayToHexStr(byte[] bytes) {

        //可以使用 commons-codec.jar的Hex类--》public static char[] encodeHex(byte[] data, boolean toLowerCase)
        int j = bytes.length;
        char[] buf = new char[j * 2];
        int k = 0;
        for (byte b : bytes) {
            buf[k++] = hexDigits[b >>> 4 & 0xf];
            buf[k++] = hexDigits[b & 0xf];
        }
        return new String(buf);
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static String byteArrayToBase64Str(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    /**
     *
     * @param key
     * @return
     */
    public static byte[] base64StrToByteArray(String key) {
        return Base64.getDecoder().decode(key.getBytes());
    }

}
