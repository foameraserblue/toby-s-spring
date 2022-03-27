package com.foameraserblue.dao;

import com.foameraserblue.User;
import com.foameraserblue.connection.ConnectionMaker;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.*;


public class UserDao {
    private DataSource dataSource;


    // 수정자 메소드를 통한 DI
    public void setConnectionMaker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = dataSource.getConnection();

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
        Connection c = dataSource.getConnection();

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
