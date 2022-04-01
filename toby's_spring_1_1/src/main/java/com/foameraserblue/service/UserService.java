package com.foameraserblue.service;

import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
import com.foameraserblue.dao.UserDao;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class UserService {
    UserDao userDao;

    PlatformTransactionManager transactionManager;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;


    public UserService(UserDao userDao,PlatformTransactionManager transactionManager) {

        this.userDao = userDao;
        this.transactionManager = transactionManager;
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

