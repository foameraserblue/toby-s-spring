package com.foameraserblue.service;

import com.foameraserblue.Level;
import com.foameraserblue.User;
import com.foameraserblue.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public class UserService {
    UserDao userDao;


    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // 사용자 레벨 업그레이드 메소드
    public void upgradeLevels() {
        List<User> users = userDao.getAll();

        for (User user : users) {
            Boolean changed = null;
            if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
                user.setLevel(Level.SILVER);
                changed = true;
            } else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
                user.setLevel(Level.GOLD);
                changed = true;

            } else if (user.getLevel() == Level.GOLD) {
                changed = false;
            } else {
                changed = false;
            }

            if (changed) {
                userDao.update(user);
            }
        }

    }

    // 신규 사용자 추가
    public void add(User user){
        if (user.getLevel() == null) user.setLevel(Level.BASIC);

        userDao.add(user);
    }
}