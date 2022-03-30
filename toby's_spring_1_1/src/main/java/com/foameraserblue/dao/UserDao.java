package com.foameraserblue.dao;

import com.foameraserblue.User;
import com.foameraserblue.dao.strategy.AddStatement;
import com.foameraserblue.dao.strategy.DeleteAllStatement;
import com.foameraserblue.dao.strategy.StatementStrategy;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.*;


public class UserDao {
    private DataSource dataSource;


    // 수정자 메소드를 통한 DI
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws SQLException {
        Connection c = dataSource.getConnection();

        StatementStrategy strategy = new AddStatement(user);
        jdbcContextWithStatementStrategy(strategy);

    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        // sql 을 담을 statement 생성
        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?"
        );
        ps.setString(1, id);

        // 만들어진 statement 실행
        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        // 사용후 리소스를 닫아준다
        rs.close();
        ps.close();
        c.close();

        if (user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

    // 전체삭제 기능을 담당하는 메서드
    public void deleteAll() throws SQLException {

        // 전략패턴에서 컨텍스트에 해당하는 부분을 따로 메서드로 독립시킴
        // jdbcContextWithStatementStrategy 메서드(컨텍스트) 에게 구체적인 전략을 전달하는는
        // 클라이언트의 역할을 하게됨
        StatementStrategy strategy = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(strategy);


    }

    // 테이블 레코드의 갯수를 알려줌
    public int getCount() throws SQLException {

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = dataSource.getConnection();

            ps = c.prepareStatement("select count(*) from users");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {

                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }
        }


    }

    // 스트레티지 패턴에서 컨텍스트에 해당하는 부분을 별도의 메서드로 독립시켰다.
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {

                }
            }
        }
    }

}
