package pwd.allen;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * @author 门那粒沙
 * @create 2021-01-30 10:58
 **/
public class PwdTest {

    /**
     * 孪生素数就是指相差为2的素数对，例如3和5，5和7，11和13……
     * 输入N，输出N以内的素数对个数，N<=1000000
     *
     *
     * 9930 204
     */
    @Test
    public void test() {
        int rel = 0;
        ArrayList<String> list = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        for (int i = 4; i <= num; i++) {
            if (set.contains(i - 2) || set.contains(i)) {
                continue;
            }
            boolean b1 = check(i - 2);
            boolean b2 = check(i);
            if (!b1) {
                set.add(i - 2);
            }
            if (!b2) {
                set.add(i);
            }
            if (b1 && b2) {
                rel++;
                list.add((i-2) + "-" + i);
            }
        }
        System.out.println(rel);
    }

    public static boolean check(int num) {
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }


}
