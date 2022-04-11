package com.foameraserblue.dao.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// 스트레티지 패턴을 이용해서 Statement 를 받아오는 부분을 상황에 맞게 사용한다
public interface StatementStrategy {
    PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
