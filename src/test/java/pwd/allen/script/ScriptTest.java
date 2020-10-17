package pwd.allen.script;

import cn.hutool.script.ScriptUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;

import javax.script.*;
import java.util.HashMap;
import java.util.List;

/**
 * 常用的表达式引擎计算方案，包含：java脚本引擎（javax/script）、groovy脚本引擎、Expression4j、Fel表达式引擎。
 * 其中java脚本引擎使用了解释执行和编译执行两种方式、groovy脚本只采用了编译执行（解释执行太慢）、Fel采用了静态参数和动态参数两种方式。
 *
 * @author 门那粒沙
 * @create 2020-10-17 21:32
 **/
public class ScriptTest {

    /**
     * 在JAVA8中，默认对JUEL 和 JavaScript提供支持，如果需要对其他脚本语言提供支持，需要引入该语言相应的JSR-223的实现包。
     * @throws ScriptException
     * @throws NoSuchMethodException
     * @throws InterruptedException
     */
    @Test
    public void script() throws ScriptException, NoSuchMethodException, InterruptedException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("javascript");

        //向上下文中存入变量
        HashMap<String, Object> paramMap = new HashMap<>();
        engine.put("msg", "just a test");
        engine.put("param", paramMap);
        //定义类user
        String str = "msg += '!!!';param.put('name','pwd');var user = {name:'tom',age:23,hobbies:['football','basketball']}; ";
        //执行脚本
        // 方式一：编译执行，效率高，可重复用
//        Compilable compilable = (Compilable) engine;
//        CompiledScript compile = compilable.compile(str);
//        compile.eval();
        // 方式二：解释执行
        engine.eval(str);

        //从上下文引擎中取值
        String msg = (String) engine.get("msg");
        ScriptObjectMirror user = (ScriptObjectMirror) engine.get("user");
        ScriptObjectMirror hobbies = (ScriptObjectMirror) user.get("hobbies");
        System.out.println(msg);
        System.out.println(user.get("name") + ":" + hobbies.get("0"));
        System.out.println(paramMap);

        //定义数学函数
        engine.eval("function add (a, b) {c = a + b; return c; }");

        // 调用函数
        // 方法一
        // 取得调用接口
        Invocable jsInvoke = (Invocable) engine;
        Object result1 = jsInvoke.invokeFunction("add", new Object[] { 10, 5 });
        System.out.println(result1);

        // 方法二
        //调用加法函数,注意参数传递的方法
        Addr adder = jsInvoke.getInterface(Addr.class);
        int result2 = adder.add(10, 35);
        System.out.println(result2);

        //定义run()函数
        engine.eval("function run() {print('定义的run()函数');}");
        Invocable invokeEngine = (Invocable) engine;
        Runnable runner = invokeEngine.getInterface(Runnable.class);

        //定义线程运行之
        Thread t = new Thread(runner);
        t.start();
        t.join();

        //导入其他java包

//        String jsCode = "importPackage(java.util);var list2 = Arrays.asList(['A', 'B', 'C']); "; //jdk1.6的写法
        String jsCode = "var list2 = java.util.Arrays.asList(['A', 'B', 'C']); ";

        engine.eval(jsCode);
        List<String> list2 = (List<String>) engine.get("list2");
        System.out.println(list2);

    }

    @Test
    public void hutool() {
        HashMap<String, Object> paramMap = new HashMap<>();
        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setAttribute("param", paramMap, ScriptContext.ENGINE_SCOPE);
        Object eval = ScriptUtil.eval("param.put('name','fuck');return param.get('name');");
        System.out.println(eval);
    }
}
