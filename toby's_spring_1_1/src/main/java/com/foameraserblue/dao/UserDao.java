package com.foameraserblue.dao;

import com.foameraserblue.domain.User;

import java.util.List;

// Dao 인터페이스 정의
public interface UserDao {
    void add(User user);

    User get(String id);

    List<User> getAll();

    void deleteAll();

    int getCount();

    void update(User user);
}
