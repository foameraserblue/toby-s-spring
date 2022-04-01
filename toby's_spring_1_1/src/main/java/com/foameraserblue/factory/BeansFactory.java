package com.foameraserblue.factory;

import com.foameraserblue.connection.ConnectionMaker;
import com.foameraserblue.connection.MysqlConnection;
import com.foameraserblue.dao.UserDaoJdbc;
import com.foameraserblue.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

import static com.foameraserblue.studyinfo.DB_ID;
import static com.foameraserblue.studyinfo.DB_PASSWORD;

// 구조를 결정하는 오브젝트
// 오브젝트의 생성과 관계설정에 대해 제어권한을 갖고있는 특별한 오브젝트의 개념
// IOC 가 적용되었다 볼 수 있음
@Configuration
public class BeansFactory {
    // UserDao 를 생성하는 책임
    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
        userDaoJdbc.setJdbcTemplate(dataSource());

        return userDaoJdbc;
    }

//    // AccountDao 를 생성하는 책임
//    @Bean
//    public AccountDao accountDao() {
//        return new AccountDao();
//    }

    // db 연결을 위한 데이터소스 빈 생성
    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/toby_1");
        dataSource.setUsername(DB_ID);
        dataSource.setPassword(DB_PASSWORD);

        return dataSource;
    }

    // 실제로 디비에 접속하게해주는 MysqlConnection 를 생성
    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new MysqlConnection();
    }

    @Bean
    public UserService userService() {
        UserService userService = new UserService(userDao());
        userService.setDataSource(dataSource());

        return userService;
    }


}
