package com.foameraserblue.dao;

import com.foameraserblue.User;
import com.foameraserblue.connection.ConnectionMaker;
import org.springframework.context.annotation.Bean;

import java.sql.*;


public class UserDao {
    // ConnectionMaker 를 인터페이스로 선언함으로써 UserDao 와 ConnectionMaker 사이의 결합도를 낮춤
    private ConnectionMaker connectionMaker;

//    // 스트레티지 패턴으로 얼마든지 ConnectionMaker 의 구현체를 런타임시에 변경할 수 있음
//    //생성자를 통한 DI
//    public UserDao(ConnectionMaker connectionMaker) {
//        this.connectionMaker = connectionMaker;
//    }

    // 수정자 메소드를 통한 DI
    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();

        // sql 을 담을 statement 생성
        PreparedStatement ps = c.prepareStatement(
                "insert into users(id,name,password) values(?,?,?)"
        );
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        // 만들어진 statement 실행
        ps.executeUpdate();

        // 사용후 리소스를 닫아준다
        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();

        // sql 을 담을 statement 생성
        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?"
        );
        ps.setString(1, id);

        // 만들어진 statement 실행
        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        // 사용후 리소스를 닫아준다
        rs.close();
        ps.close();
        c.close();

        return user;
    }
}
