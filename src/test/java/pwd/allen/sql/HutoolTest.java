package pwd.allen.sql;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 会根据依赖自动选择连接池，配置路径默认config/db.setting
 * 连接池优先级：Hikari > Druid > Tomcat > Dbcp > C3p0 > Hutool Pooled
 * @author 门那粒沙
 * @create 2020-10-18 18:55
 **/
public class HutoolTest {

    @Data
    class User {
        private Integer id;
        private String userName;
        private Integer age;
        private Integer status;
        private Integer deptId;
        private Date createAt;
        private String msg;
    }

    private Db db;

    private List<User> list;

    @Before
    public void init() {
        // 默认读取db.setting，使用PooledDatasource数据源
        db = Db.use();

        list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setAge(20 + i);
            user.setUserName("hutool" + i);
            user.setCreateAt(new Date());
            user.setMsg("add by hutool：" + i);
            list.add(user);
        }
    }

    @Test
    public void add() throws SQLException {
        Entity entity = Entity.create("db_user")
                .parseBean(list.get(0), true, true);
//        int insert = db.insert(entity);
        Long key = db.insertForGeneratedKey(entity);
        System.out.println("插入成功，主键为：" + key);
    }

    @Test
    public void addBatch() throws SQLException {
        List<Entity> entities = new ArrayList<>();
        for (User user : list) {
            Entity entity = Entity.create("db_user")
                    .parseBean(user, true, true);
            entities.add(entity);
        }
        int[] insert = db.insert(entities);
        System.out.println("插入成功：" + Arrays.toString(insert));
    }

    @Test
    public void query() throws SQLException {
        List<User> query = db.query("select * from db_user where age>?", User.class, 20);
        System.out.println(query);

        User user = db.queryOne("select * from db_user where id=?", 1).toBeanWithCamelCase(new User());
        System.out.println(user);
    }

}
