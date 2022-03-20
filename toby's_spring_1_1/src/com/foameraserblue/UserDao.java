package com.foameraserblue;

import java.sql.*;

import static com.foameraserblue.studyinfo.DB_ID;
import static com.foameraserblue.studyinfo.DB_PASSWORD;

public class UserDao {

    public void add(User user) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // db 연결을 위한 커넥션 생성
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost/toby_1", DB_ID, DB_PASSWORD
        );

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
        Class.forName("com.mysql.cj.jdbc.Driver");

        // db 연결을 위한 커넥션 생성
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost/toby_1", DB_ID, DB_PASSWORD
        );

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
