package pwd.allen.gitlab;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author lenovo
 * @create 2021-06-17 12:52
 **/
public class GitTest {

    static final String parentPath = "F:/project/control/control_code/";

    @Test
    public void gitlab_ci() {
//        String path_map = "classpath:gitlab/path_map-gd-pf.json";
//        String path_dep = "classpath:gitlab/path_dep-gd-pf.json";
//        List<String> onlyBuilds = Arrays.asList("Common");

        String path_map = "classpath:gitlab/path_map-gd.json";
        String path_dep = "classpath:gitlab/path_dep-gd.json";
        List<String> onlyBuilds = Arrays.asList("Shardbatis");


        JSONObject jsonObject_path = JSONUtil.parseObj(FileUtil.readUtf8String(path_map));
        Map<String, String> map_path = sortByComparator(jsonObject_path);

        JSONObject jsonObject_dep = JSONUtil.parseObj(FileUtil.readUtf8String(path_dep));
        Map<String, Set<String>> map_depByRecursion = getRelationByRecursion(jsonObject_dep);

        Yaml yaml = new Yaml();
        Map<String, String> load = (Map)yaml.load(FileUtil.getInputStream("classpath:gitlab/gitlab-ci.yml"));

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : load.entrySet()) {
            String key = entry.getKey();
            String template = entry.getValue();
            sb.append(String.format("##########################%s###############################\n", key));
            String[] parts = template.split("\\u0020*##\n?");
            sb.append(parts[0]);
            template = parts[1];
            for (String path : map_path.keySet()) {
                if (!"all_build,build,cache".contains(key) && onlyBuilds.contains(path)) {
                    continue;
                }
                String[] split = path.split("/");
                String str = template;
                if ("cache".equals(key)) {
                    StringBuilder sb_cache = new StringBuilder();
                    sb_cache.append(String.format("\u0020\u0020\u0020\u0020\u0020\u0020- %s/**/*\n", path));
                    Set<String> set = map_depByRecursion.get(path);
                    if (CollectionUtils.isNotEmpty(set)) {
                        for (String o : set) {
                            sb_cache.append(String.format("\u0020\u0020\u0020\u0020\u0020\u0020- %s/**/*\n", o));
                        }
                    }
                    str = str.replaceAll("\\{cache\\}\n", sb_cache.toString());
                }
                if ("build".equals(key)) {
                    Set<String> set = map_depByRecursion.get(path);
                    if (CollectionUtils.isNotEmpty(set)) {
                        StringBuilder sb_need = new StringBuilder();
                        sb_need.append("\u0020\u0020needs:\n");
                        for (String o : set) {
                            sb_need.append(String.format("\u0020\u0020\u0020\u0020- job: \"%s_build\"\n", o.split("/")[0]));
                        }
                        str = str.replaceAll("\\{needs\\}\n", sb_need.toString());
                    } else {
                        str = str.replaceAll("\\{needs\\}\n", "");

                    }
                }
                str = str.replaceAll("\\{path\\}", path);
                str = str.replaceAll("\\{path_one\\}", split.length > 1 ? split[0] : path);
                str = str.replaceAll("\\{path_two\\}", split.length > 1 ? split[1] : path);
                str = str.replaceAll("\\{path_two_low\\}", split.length > 1 ? split[1].toLowerCase() : path.toLowerCase());
                str = str.replaceAll("\\{num\\}", map_path.get(path));
                sb.append(str);
            }
            if (parts.length >= 3) {
                sb.append(parts[2]);
            }
            sb.append("\n\n\n");
        }

        System.out.println(sb.toString());
    }

    @Test
    public void getPathsTest() {
        System.out.println(JSONUtil.toJsonPrettyStr(getPaths()));
    }
    public static Map<String, String> getPaths() {
        List<File> files = FileUtil.loopFiles(new File(parentPath), 3, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().contains("\\execute")) {
                    return true;
                }
                return false;
            }
        });

        Map<String, String> map_rel = MapUtil.<String, String>builder().build();
        for (File file : files) {
            map_rel.put(file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(String.format("(%s|/execute.*)", parentPath), ""), "1");
        }
        return map_rel;
    }

    @Test
    public void getRelationsTest() {
        System.out.println(JSONUtil.toJsonPrettyStr(getRelations()));
    }
    public static Map<String, Set<String>> getRelations() {
        JSONObject jsonObject_path = JSONUtil.parseObj(FileUtil.readUtf8String("classpath:gitlab/path_map.json"));
        Map<String, String> map_path = sortByComparator(jsonObject_path);

        HashMap<String, Set<String>> map = new HashMap<>();
        for (String path : map_path.keySet()) {
            String content = FileUtil.readUtf8String(parentPath + path + "/pom.xml");
            for (String temp : map_path.keySet()) {
                if (temp.equals(path)) {
                    continue;
                }
                String[] split = temp.split("/");
                String regex = String.format("<artifactId>%s[^<]*</artifactId>", (split.length > 1 ? split[1] : split[0]).replace("_Service", ""));
                Pattern compile = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                if (compile.matcher(content).find()) {
                    Set<String> set = map.get(path);
                    if (set == null) {
                        set = new HashSet<>();
                        map.put(path, set);
                    }
                    set.add(temp);
                }
            }
        }
        return map;
    }

    @Test
    public void getRelations2Test() {
        System.out.println(JSONUtil.toJsonPrettyStr(getRelationByRecursion(JSONUtil.parseObj(FileUtil.readUtf8String("classpath:gitlab/path_dep.json")))));
    }
    public static Map<String, Set<String>> getRelationByRecursion(JSONObject jsonObject_dep) {
        HashMap<String, Set<String>> map_rel = new HashMap<>();
        for (String path : jsonObject_dep.keySet()) {
            HashSet<String> set_dep = new HashSet<>();
            map_rel.put(path, set_dep);
            recursion(jsonObject_dep, path, set_dep);
        }
        return map_rel;
    }
    public static void recursion(JSONObject jsonObject_dep, String path, Set<String> set_dep) {
        JSONArray jsonArray = jsonObject_dep.getJSONArray(path);
        if (jsonArray != null) {
            for (Object o : jsonArray) {
                set_dep.add(o.toString());
                recursion(jsonObject_dep, o.toString(), set_dep);
            }
        } else {
            set_dep.add(path);
        }
    }

    @Test
    public void sortByComparatorTest() {
        JSONObject jsonObject_path = JSONUtil.parseObj(FileUtil.readUtf8String("classpath:gitlab/path_map-gd.json"));
        Map map = sortByComparator(jsonObject_path);
        System.out.println(map);
    }
    public static Map<String, String> sortByComparator(Map unsortMap){
        List list = new LinkedList(unsortMap.entrySet());
        Collections.sort(list, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return -((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        Map sortedMap = new LinkedHashMap();

        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;

    }

}
