import com.foameraserblue.dao.UserDao;
import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
import com.foameraserblue.service.UserService;
import com.foameraserblue.service.UserServiceImpl;
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
import testclass.TestUserServiceImpl;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.foameraserblue.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.foameraserblue.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    UserService userService;
    //    UserServiceImpl userServiceImpl;
    UserDao userDao;
    DataSource dataSource;
    PlatformTransactionManager transactionManager;
    UserService testUserService;
    private static ApplicationContext context;

    List<User> users;

    @BeforeClass
    public static void setUpClass() {

        context = new AnnotationConfigApplicationContext(TestFactory.class);
    }

    @Before
    public void setUp() {
        userService = context.getBean("userService", UserService.class);
//        userServiceImpl = context.getBean("userServiceImpl", UserServiceImpl.class);
        userDao = context.getBean("userDao", UserDao.class);
        dataSource = context.getBean("dataSource", DataSource.class);
        transactionManager = context.getBean("transactionManager", PlatformTransactionManager.class);
        testUserService = context.getBean("testUserService", UserService.class);

        users = Arrays.asList(
                new User("1?????????", "1?????????", "1ps", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "foameraserblue@gmail.com"),
                new User("2?????????", "2?????????", "2ps", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "foameraserblue@gmail.com"),
                new User("3?????????", "3?????????", "3ps", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER + 10, MIN_RECCOMEND_FOR_GOLD - 1, "foameraserblue@gmail.com"),
                new User("4?????????", "4?????????", "4ps", Level.SILVER, MIN_LOGCOUNT_FOR_SILVER + 10, MIN_RECCOMEND_FOR_GOLD, "foameraserblue@gmail.com"),
                new User("5?????????", "5?????????", "5ps", Level.GOLD, MIN_LOGCOUNT_FOR_SILVER + 50, Integer.MAX_VALUE, "foameraserblue@gmail.com")
        );
    }


    @Test
    @DirtiesContext // ??????????????? di ??? ???????????? ??????????????? ??????
    public void upgradeLevels() throws SQLException {
        // ?????? DAO ??????
        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        // ?????? MailSender ??????
        MailSender mockMailSender = mock(MailSender.class);
        // ????????? ?????????????????? ????????? ??????????????? ?????? ???????????????
        // ????????? ?????????????????? ??????????????? ???????????? ????????? ???????????? ????????? Bean ??? ?????? DI ?????? ????????? ??????
        UserServiceImpl userServiceImpl = new UserServiceImpl(mockUserDao, mockMailSender);

        // ???????????? ?????? ??????
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
    public void upgradeAllOrNothing() throws SQLException {


        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceImpl.TestUserServiceException ignored) {

        }

        checkLevelUpgraded(users.get(1), false);
    }

//    // ???????????? ?????? ????????????
//    public static class TestUserServiceImpl extends UserServiceImpl {
//        private String id = "3?????????"; // ????????? ???????????? ?????????
//
//        public TestUserServiceImpl(UserDao userDao, MailSender mailSender) {
//            super(userDao, mailSender);
//
//        }
//
//        @Override
//        protected void upgradeLevel(User user) {
//            if (user.getId().equals(this.id)) throw new TestUserServiceException();
//            super.upgradeLevel(user);
//        }
//    }
//


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
            Assert.assertEquals(user.getLevel().nextLevel(), userUpdate.getLevel());
        } else {
            Assert.assertEquals(user.getLevel(), userUpdate.getLevel());
        }
    }


}
