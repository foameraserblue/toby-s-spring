import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
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

import static com.foameraserblue.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.foameraserblue.service.UserService.MIN_RECCOMEND_FOR_GOLD;

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
                new User("1번아디", "1번이름", "1ps", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("2번아디", "2번이름", "2ps", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("3번아디", "3번이름", "3ps", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER + 10, MIN_RECCOMEND_FOR_GOLD - 1),
                new User("4번아디", "4번이름", "4ps", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER + 10, MIN_RECCOMEND_FOR_GOLD),
                new User("5번아디", "5번이름", "5ps", Level.GOLD, MIN_LOGCOUNT_FOR_SILVER + 50, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() {
        userDao.deleteAll();
        for (User user : users) userDao.add(user);


        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);


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

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());

        if (upgraded) {
            Assert.assertEquals(userUpdate.getLevel(), user.getLevel().nextLevel());
        } else {
            Assert.assertEquals(userUpdate.getLevel(), user.getLevel());
        }
    }


}
