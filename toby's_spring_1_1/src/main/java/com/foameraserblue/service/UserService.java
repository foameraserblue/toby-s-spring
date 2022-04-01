package com.foameraserblue.service;

import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
import com.foameraserblue.dao.UserDao;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class UserService {
    UserDao userDao;
    DataSource dataSource;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;


    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 사용자 레벨 업그레이드 메소드
    public void upgradeLevels() throws SQLException {
        // 트랜잭션 동기화작업 초기화
        TransactionSynchronizationManager.initSynchronization();
        // db 커넥션을 생성하고 트랜잭션 시작, 이후의 작업은 모두 이 트랜잭션 내에서 시작한다
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<User> users = userDao.getAll();

            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            // 모든 작업이 정상적으로 실행됐을경우 트랜잭션을 커밋한다
            c.commit();
        } catch (Exception e) {
            // 예외가 발생시 롤백한다.
            c.rollback();
            throw e;
        } finally {
            // 데이터소스 유틸을 이용해 안전하게 db 커넥션을 닫는다
            DataSourceUtils.releaseConnection(c, dataSource);
            // 동기화작업 종료 및 마무리
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
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

