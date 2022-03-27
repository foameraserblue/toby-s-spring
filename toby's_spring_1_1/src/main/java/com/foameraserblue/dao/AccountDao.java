package com.foameraserblue.dao;

import com.foameraserblue.connection.ConnectionMaker;

public class AccountDao {
    // ConnectionMaker 를 인터페이스로 선언함으로써 AccountDao 와 ConnectionMaker 사이의 결합도를 낮춤
    private final ConnectionMaker connectionMaker;

    // 스트레티지 패턴으로 얼마든지 ConnectionMaker 의 구현체를 런타임시에 변경할 수 있음
    public AccountDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
}
