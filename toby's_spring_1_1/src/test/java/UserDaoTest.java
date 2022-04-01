import com.foameraserblue.Level;
import com.foameraserblue.User;
import com.foameraserblue.dao.UserDao;
import com.foameraserblue.dao.UserDaoJdbc;
import com.foameraserblue.factory.BeansFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserDaoTest {

    private UserDao userDaoJdbc;
    private User user1;
    private User user2;
    private User user3;
    private static ApplicationContext context;

    @BeforeClass
    public static void setUpClass() {
        context = new AnnotationConfigApplicationContext(BeansFactory.class);
    }

    @Before
    public void setUp() {
        System.out.println(context);
        System.out.println(this);

        userDaoJdbc = context.getBean("userDao", UserDaoJdbc.class);

        user1 = new User("아디1", "이름1", "비번1", Level.BASIC, 1, 0);
        user2 = new User("아디2", "이름2", "비번2", Level.SILVER, 55, 10);
        user3 = new User("아디3", "이름3", "비번3", Level.GOLD, 100, 40);
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {

        // 삭제 메서드를 추가해서 테이블 갯수확인하는 작업
        userDaoJdbc.deleteAll();
        Assert.assertEquals(userDaoJdbc.getCount(), 0);


        userDaoJdbc.add(user1);
        userDaoJdbc.add(user2);
        Assert.assertEquals(userDaoJdbc.getCount(), 2);

        User userGet1 = userDaoJdbc.get(user1.getId());
        checkSameUser(userGet1, user1);

        User userGet2 = userDaoJdbc.get(user2.getId());
        checkSameUser(userGet2, user2);
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {


        userDaoJdbc.deleteAll();
        Assert.assertEquals(userDaoJdbc.getCount(), 0);

        userDaoJdbc.add(user1);
        Assert.assertEquals(userDaoJdbc.getCount(), 1);

        userDaoJdbc.add(user2);
        Assert.assertEquals(userDaoJdbc.getCount(), 2);

        userDaoJdbc.add(user3);
        Assert.assertEquals(userDaoJdbc.getCount(), 3);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFail() throws SQLException {

        userDaoJdbc.deleteAll();
        Assert.assertEquals(userDaoJdbc.getCount(), 0);

        userDaoJdbc.get("이상한아이디");

    }

    @Test(expected = DataAccessException.class)
    public void duplicateKey() {
        userDaoJdbc.deleteAll();

        userDaoJdbc.add(user1);
        userDaoJdbc.add(user1);
    }

    @Test
    public void update() {
        userDaoJdbc.deleteAll();

        userDaoJdbc.add(user1);
        userDaoJdbc.add(user2);

        user1.setName("토비짱");
        user1.setPassword("토비비번");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(9999);
        userDaoJdbc.update(user1);

        User user1Update = userDaoJdbc.get(user1.getId());
        checkSameUser(user1, user1Update);

        User user2same = userDaoJdbc.get(user2.getId());
        checkSameUser(user2, user2same);
    }

    // 필드값 검증 메서드
    private void checkSameUser(User user1, User user2) {
        Assert.assertEquals(user1.getId(), user2.getId());
        Assert.assertEquals(user1.getName(), user2.getName());
        Assert.assertEquals(user1.getPassword(), user2.getPassword());
        Assert.assertEquals(user1.getLevel(), user2.getLevel());
        Assert.assertEquals(user1.getLogin(), user2.getLogin());
        Assert.assertEquals(user1.getRecommend(), user2.getRecommend());

    }


}
