package com.foameraserblue.service;

import com.foameraserblue.domain.Level;
import com.foameraserblue.domain.User;
import com.foameraserblue.dao.UserDao;

import java.util.List;


public class UserService {
    UserDao userDao;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;


    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // 사용자 레벨 업그레이드 메소드
    public void upgradeLevels() {
        List<User> users = userDao.getAll();

        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }

    }

    // 신규 사용자 추가
    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);

        userDao.add(user);
    }

    private void upgradeLevel(User user) {
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
