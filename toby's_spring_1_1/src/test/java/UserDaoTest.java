import com.foameraserblue.User;
import com.foameraserblue.dao.UserDao;
import com.foameraserblue.factory.DaoFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

public class UserDaoTest {

    private UserDao userDao;
    private User user1;
    private User user2;
    private User user3;
    private static ApplicationContext context;

    @BeforeClass
    public static void setUpClass() {
        context = new AnnotationConfigApplicationContext(DaoFactory.class);
    }

    @Before
    public void setUp() {
        System.out.println(context);
        System.out.println(this);

        userDao = context.getBean("userDao", UserDao.class);

        user1 = new User("아디1", "이름1", "비번1");
        user2 = new User("아디2", "이름2", "비번2");
        user3 = new User("아디3", "이름3", "비번3");
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {

        // 삭제 메서드를 추가해서 테이블 갯수확인하는 작업
        userDao.deleteAll();
        Assert.assertEquals(userDao.getCount(), 0);


        userDao.add(user1);
        userDao.add(user2);
        Assert.assertEquals(userDao.getCount(), 2);

        User userGet1 = userDao.get(user1.getId());
        Assert.assertEquals(userGet1.getName(), user1.getName());
        Assert.assertEquals(userGet1.getPassword(), user1.getPassword());

        User userGet2 = userDao.get(user2.getId());
        Assert.assertEquals(userGet2.getName(), user2.getName());
        Assert.assertEquals(userGet2.getPassword(), user2.getPassword());
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {


        userDao.deleteAll();
        Assert.assertEquals(userDao.getCount(), 0);

        userDao.add(user1);
        Assert.assertEquals(userDao.getCount(), 1);

        userDao.add(user2);
        Assert.assertEquals(userDao.getCount(), 2);

        userDao.add(user3);
        Assert.assertEquals(userDao.getCount(), 3);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFail() throws SQLException {

        userDao.deleteAll();
        Assert.assertEquals(userDao.getCount(), 0);

        userDao.get("이상한아이디");

    }

}
