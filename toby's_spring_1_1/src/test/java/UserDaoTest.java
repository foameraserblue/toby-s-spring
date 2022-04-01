import com.foameraserblue.User;
import com.foameraserblue.dao.UserDao;
import com.foameraserblue.dao.UserDaoJdbc;
import com.foameraserblue.factory.DaoFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

public class UserDaoTest {

    private UserDao userDaoJdbc;
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

        userDaoJdbc = context.getBean("userDao", UserDaoJdbc.class);

        user1 = new User("아디1", "이름1", "비번1");
        user2 = new User("아디2", "이름2", "비번2");
        user3 = new User("아디3", "이름3", "비번3");
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
        Assert.assertEquals(userGet1.getName(), user1.getName());
        Assert.assertEquals(userGet1.getPassword(), user1.getPassword());

        User userGet2 = userDaoJdbc.get(user2.getId());
        Assert.assertEquals(userGet2.getName(), user2.getName());
        Assert.assertEquals(userGet2.getPassword(), user2.getPassword());
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
    public void duplicateKey(){
        userDaoJdbc.deleteAll();

        userDaoJdbc.add(user1);
        userDaoJdbc.add(user1);
    }

}
