package testclass;

import com.foameraserblue.dao.UserDao;
import com.foameraserblue.domain.User;
import com.foameraserblue.service.UserServiceImpl;
import org.springframework.mail.MailSender;

public class TestUserServiceImpl extends UserServiceImpl {
    String id = "4번아디";

    public TestUserServiceImpl(UserDao userDao, MailSender mailSender) {
        super(userDao, mailSender);

    }

    @Override
    protected void upgradeLevel(User user) {
        System.out.println("테스트");
        // 예외를 발생시킬 인덱스
        if (user.getId().equals(id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

    // 테스트를 위한 가짜 exception
    public static class TestUserServiceException extends RuntimeException {
    }

}
