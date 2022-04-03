package com.foameraserblue.service;

import com.foameraserblue.dao.UserDao;
import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.List;


public class UserService {
    UserDao userDao;

    // 서비스 추상화된 인터페이스의 구체적인 객체를 DI 를 통해 주입받는다.
    PlatformTransactionManager transactionManager;
    MailSender mailSender;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;


    public UserService(UserDao userDao, PlatformTransactionManager transactionManager, MailSender mailSender) {

        this.userDao = userDao;
        this.transactionManager = transactionManager;
        this.mailSender = mailSender;
    }

    // 필요시 di 설정 변경을 위한 세터
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 사용자 레벨 업그레이드 메소드
    public void upgradeLevels() throws SQLException {

        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();

            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            // 모든 작업이 정상적으로 실행됐을경우 트랜잭션을 커밋한다
            transactionManager.commit(status);
        } catch (Exception e) {
            // 예외가 발생시 롤백한다.
            transactionManager.rollback(status);
            throw e;
        }
    }

    // 신규 사용자 추가
    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);

        userDao.add(user);
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private void sendUpgradeEmail(User user) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("foameraserblue@gmail.com");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        mailSender.send(mailMessage);

    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();

        switch (currentLevel) {
            case BASIC:
                return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("Unknown Level : " + currentLevel);
        }
    }
}

