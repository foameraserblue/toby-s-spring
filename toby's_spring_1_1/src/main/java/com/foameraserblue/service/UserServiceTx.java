package com.foameraserblue.service;

import com.foameraserblue.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

// 트랜잭션 처리를 위한 객체
// 비지니스 로직에 대해선 일체 관여하지 않는다
public class UserServiceTx implements UserService {
    UserService userService;
    // 트랜잭션 관리를 위한 인터페이스
    PlatformTransactionManager transactionManager;

    // 같은 UserService 를 구현한 다른 오브젝트를 di 받는다
    public UserServiceTx(UserService userService, PlatformTransactionManager transactionManager) {
        this.userService = userService;
        this.transactionManager = transactionManager;
    }

    @Override
    public void add(User user) {
        // 비즈니스 로직은, 비지니스 로직 구현체에게 기능을 위임한다
        userService.add(user);

    }

    @Override
    public void upgradeLevels() throws SQLException {
        TransactionStatus status = this.transactionManager
                .getTransaction(new DefaultTransactionDefinition());
        try {
            // 비즈니스 로직은, 비지니스 로직 구현체에게 기능을 위임한다
            userService.upgradeLevels();

            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;

        }
    }
}
