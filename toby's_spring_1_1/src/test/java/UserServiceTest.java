import com.foameraserblue.dao.UserDao;
import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
import com.foameraserblue.service.UserService;
import com.foameraserblue.service.UserServiceImpl;
import com.foameraserblue.service.UserServiceTx;
import mock.MockMailSender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import testFactory.TestFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.foameraserblue.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.foameraserblue.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class UserServiceTest {

    UserService userService;
    UserServiceImpl userServiceImpl;
    UserDao userDao;
    DataSource dataSource;
    private static ApplicationContext context;

    List<User> users;

    @BeforeClass
    public static void setUpClass() {

        context = new AnnotationConfigApplicationContext(TestFactory.class);
    }

    @Before
    public void setUp() {
        userService = context.getBean("userService", UserService.class);
        userServiceImpl = context.getBean("userServiceImpl", UserServiceImpl.class);
        userDao = context.getBean("userDao", UserDao.class);
        dataSource = context.getBean("dataSource", DataSource.class);

        users = Arrays.asList(
                new User("1번아디", "1번이름", "1ps", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "foameraserblue@gmail.com"),
                new User("2번아디", "2번이름", "2ps", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "foameraserblue@gmail.com"),
                new User("3번아디", "3번이름", "3ps", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER + 10, MIN_RECCOMEND_FOR_GOLD - 1, "foameraserblue@gmail.com"),
                new User("4번아디", "4번이름", "4ps", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER + 10, MIN_RECCOMEND_FOR_GOLD, "foameraserblue@gmail.com"),
                new User("5번아디", "5번이름", "5ps", Level.GOLD, MIN_LOGCOUNT_FOR_SILVER + 50, Integer.MAX_VALUE, "foameraserblue@gmail.com")
        );
    }

    // 테스트를 위한 가짜객체
    static class TestUserService extends UserServiceImpl {
        private String id; // 예외를 발생시킬 인덱스

        public TestUserService(UserDao userDao, MailSender mailSender, String id) {
            super(userDao, mailSender);
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    // 테스트를 위한 가짜 exception
    public static class TestUserServiceException extends RuntimeException {
    }


    @Test
    @DirtiesContext // 컨텍스트의 di 를 변경하는 테스트라는 의미
    public void upgradeLevels() throws SQLException {
        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        // 필요한 mock 객체로 di 객체를 변경해준다
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);


        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> request = mockMailSender.getRequest();
        assertEquals(request.size(), 2);
        assertEquals(request.get(0), users.get(1).getEmail());
        assertEquals(request.get(1), users.get(3).getEmail());


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

    @Test
    public void upgradeAllOrNothing() {
        UserService testUserService = new TestUserService(userDao, context.getBean("mailSender", MailSender.class), users.get(3).getId());

        UserServiceTx userServiceTx = new UserServiceTx(testUserService, context.getBean("transactionManager", PlatformTransactionManager.class));


        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        try {
            userServiceTx.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException | SQLException e) {

        }

        checkLevelUpgraded(users.get(1), false);
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
