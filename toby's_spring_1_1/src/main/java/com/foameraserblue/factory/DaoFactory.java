package com.foameraserblue.factory;

import com.foameraserblue.User;
import com.foameraserblue.connection.ConnectionMaker;
import com.foameraserblue.connection.CountingConnectionMaker;
import com.foameraserblue.connection.MysqlConnection;
import com.foameraserblue.dao.AccountDao;
import com.foameraserblue.dao.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 구조를 결정하는 오브젝트
// 오브젝트의 생성과 관계설정에 대해 제어권한을 갖고있는 특별한 오브젝트의 개념
// IOC 가 적용되었다 볼 수 있음
@Configuration
public class DaoFactory {
    // UserDao 를 생성하는 책임
    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setConnectionMaker(connectionMaker());
        return userDao;
    }

    // AccountDao 를 생성하는 책임
    @Bean
    public AccountDao accountDao() {
        return new AccountDao(connectionMaker());
    }

    // 카운터를 확인시켜주는 CountingConnectionMaker 를 생성
    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    // 실제로 디비에 접속하게해주는 MysqlConnection 를 생성
    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new MysqlConnection();
    }
}
