package com.foameraserblue.service;

import com.foameraserblue.domain.User;

import java.sql.SQLException;

public interface UserService {
    void add(User user);

    void upgradeLevels() ;
}
