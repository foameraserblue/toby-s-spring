import com.foameraserblue.Level;
import com.foameraserblue.User;
import com.foameraserblue.dao.UserDao;
import com.foameraserblue.factory.BeansFactory;
import com.foameraserblue.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;

public class UserServiceTest {

    UserService userService;
    UserDao userDao;
    private static ApplicationContext context;

    List<User> users;

    @BeforeClass
    public static void setUpClass() {

        context = new AnnotationConfigApplicationContext(BeansFactory.class);
    }

    @Before
    public void setUp() {
        userService = context.getBean("userService", UserService.class);
        userDao = context.getBean("userDao", UserDao.class);

        users = Arrays.asList(
                new User("1번아디", "1번이름", "1ps", Level.BASIC, 49, 0),
                new User("2번아디", "2번이름", "2ps", Level.BASIC, 50, 0),
                new User("3번아디", "3번이름", "3ps", Level.SILVER, 60, 29),
                new User("4번아디", "4번이름", "4ps", Level.SILVER, 60, 30),
                new User("5번아디", "5번이름", "5ps", Level.GOLD, 100, 100)
        );
    }

    @Test
    public void upgradeLevels() {
        userDao.deleteAll();
        for (User user : users) userDao.add(user);


        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);


    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        Assert.assertEquals(userWithLevel.getLevel(), userWithLevelRead.getLevel());
        Assert.assertEquals(userWithoutLevel.getLevel(), userWithoutLevelRead.getLevel());
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        Assert.assertEquals(userUpdate.getLevel(), expectedLevel);
    }


}
