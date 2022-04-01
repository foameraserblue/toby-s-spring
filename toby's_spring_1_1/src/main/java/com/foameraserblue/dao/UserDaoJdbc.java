package com.foameraserblue.dao;

import com.foameraserblue.Level;
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


public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setLevel(Level.valueOf(rs.getInt("level")));
                    user.setLogin(rs.getInt("login"));
                    user.setRecommend(rs.getInt("recommend"));
                    return user;
                }
            };


    // 수정자 메소드를 통한 DI
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }


    public void add(final User user) {

        this.jdbcTemplate.update("insert into users(id,name,password,level,login,recommend) value(?,?,?,?,?,?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());

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
                        user.setLevel(Level.valueOf(resultSet.getInt("level")));
                        user.setLogin(resultSet.getInt("login"));
                        user.setRecommend(resultSet.getInt("recommend"));

                        return user;
                    }
                });

    }

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
    }

    // 전체삭제 기능을 담당하는 메서드
    public void deleteAll() {

        this.jdbcTemplate.update("delete from users");


    }

    // 테이블 레코드의 갯수를 알려줌
    public int getCount() {

        return this.jdbcTemplate.queryForInt("select count(*) from users");


    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                "update users set name = ?, password = ?, level = ?, login = ?, recommend = ? " +
                        "where id = ?", user.getName(), user.getPassword(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId()
        );
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
