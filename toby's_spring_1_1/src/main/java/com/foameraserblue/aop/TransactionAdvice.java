package com.foameraserblue.aop;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Method;

public class TransactionAdvice implements MethodInterceptor {
    PlatformTransactionManager transactionManager;

    public TransactionAdvice(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // MethodInvocation 이라는 콜백 오브젝트를 프록시로부터 받음
    // 이덕분에 어드바이스는 특정 타겟 객체에 의존하지 않고 사용이 가능함 ( 타겟 객체가 어떤건지 알 필요가없음 )
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("어드바이스");
        TransactionStatus status = this.transactionManager.getTransaction(
                new DefaultTransactionDefinition()
        );

        try {
            // 콜백을 호출해서 다겟 메소드 실행
            // 메소드 호출 전 후로 부가기능을 넣어줌
            Object ret = methodInvocation.proceed();
            this.transactionManager.commit(status);
            return ret;
        } catch (RuntimeException e) {
            System.out.println("롤백");
            this.transactionManager.rollback(status);
            throw e;
        }

    }


}
