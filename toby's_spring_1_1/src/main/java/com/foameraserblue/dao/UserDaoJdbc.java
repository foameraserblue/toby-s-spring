package com.foameraserblue.dao;

import com.foameraserblue.User;
import com.foameraserblue.dao.strategy.AddStatement;
import com.foameraserblue.dao.strategy.DeleteAllStatement;
import com.foameraserblue.dao.strategy.StatementStrategy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;


public class UserDaoJdbc implements UserDao{

    private JdbcTemplate jdbcTemplate;


    // 수정자 메소드를 통한 DI
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }


    public void add(final User user) {

        this.jdbcTemplate.update("insert into users(id,name,password) value(?,?,?)",
                user.getId(), user.getName(), user.getPassword());

    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet resultSet, int i) throws SQLException {
                        User user = new User();
                        user.setId(resultSet.getString("id"));
                        user.setName(resultSet.getString("name"));
                        user.setPassword(resultSet.getString("password"));
                        return user;
                    }
                });

    }

    @Override
    public List<User> getAll() {
        return null;
    }

    // 전체삭제 기능을 담당하는 메서드
    public void deleteAll() {

        this.jdbcTemplate.update("delete from users");


    }

    // 테이블 레코드의 갯수를 알려줌
    public int getCount() {

        return this.jdbcTemplate.queryForInt("select count(*) from users");


    }

    // 스트레티지 패턴에서 컨텍스트에 해당하는 부분을 별도의 메서드로 독립시켰다.
//    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
//        Connection c = null;
//        PreparedStatement ps = null;
//        try {
//            c = dataSource.getConnection();
//
//            ps = stmt.makePreparedStatement(c);
//
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            if (ps != null) {
//                try {
//                    ps.close();
//                } catch (SQLException e) {
//
//                }
//            }
//            if (c != null) {
//                try {
//                    c.close();
//                } catch (SQLException e) {
//
//                }
//            }
//        }
//    }


}
