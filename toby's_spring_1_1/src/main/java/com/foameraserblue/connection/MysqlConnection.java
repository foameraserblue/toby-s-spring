package com.foameraserblue.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.foameraserblue.studyinfo.DB_ID;
import static com.foameraserblue.studyinfo.DB_PASSWORD;


public class MysqlConnection implements ConnectionMaker {
    // 구체적인 db 커넥션의 구현은 ConnectionMaker 의 서브클래스가 담당
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // db 연결을 위한 커넥션 생성
        return DriverManager.getConnection(
                "jdbc:mysql://localhost/toby_1", DB_ID, DB_PASSWORD
        );
    }
}
