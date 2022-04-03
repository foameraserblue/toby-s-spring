import com.foameraserblue.dao.UserDao;
import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
import com.foameraserblue.service.UserService;
import com.foameraserblue.service.UserServiceImpl;
import com.foameraserblue.service.UserServiceTx;
import mock.MockMailSender;
import mock.MockUserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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
import static org.mockito.Mockito.*;

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
        // 가짜 DAO 객체
        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        // 가짜 MailSender 객체
        MailSender mockMailSender = mock(MailSender.class);
        // 고립된 테스트에서는 테스트 오브젝트를 직접 생성해준다
        // 고립된 테스트에서는 독립적으로 동작하는 대상을 사용하기 때문에 Bean 을 통해 DI 받을 필요가 없다
        UserServiceImpl userServiceImpl = new UserServiceImpl(mockUserDao, mockMailSender);

        // 비지니스 로직 실행
        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        Assert.assertEquals(users.get(1).getLevel(), Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        Assert.assertEquals(users.get(3).getLevel(), Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        Assert.assertEquals(mailMessages.get(0).getTo()[0], users.get(1).getEmail());
        Assert.assertEquals(mailMessages.get(1).getTo()[0], users.get(3).getEmail());


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

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        Assert.assertEquals(updated.getId(), expectedId);
        Assert.assertEquals(updated.getLevel(), expectedLevel);
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
