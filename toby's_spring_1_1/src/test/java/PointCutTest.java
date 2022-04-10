import dynamicproxy.Hello;
import dynamicproxy.HelloTarget;
import dynamicproxy.UppercaseAdvice;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import static org.junit.Assert.*;

public class PointCutTest {

    @Test
    public void classNamePointcutAdvisor() {
        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            // 익명 내부 클래스 방식으로 클래스를 정의
            @Override
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    // 클래스 이름이 HelloT 로 시작하는것만 선정한다
                    @Override
                    public boolean matches(Class<?> aClass) {
                        return aClass.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        };

        // 메소드 명이 sayH 로 시작하는것만 선정한다
        classMethodPointcut.setMappedName("sayH*");

        // 테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget {
        }
        ;
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget {
        }
        ;
        checkAdviced(new HelloToby(), classMethodPointcut, true);


    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean advised) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        if (advised) {
            assertEquals(proxiedHello.sayHello("eraser"), "HELLO ERASER");
            assertEquals(proxiedHello.sayHi("eraser"), "HI ERASER");
            assertEquals(proxiedHello.sayThankYou("eraser"), "Thank You eraser");
        } else {
            assertEquals(proxiedHello.sayHello("eraser"), "Hello eraser");
            assertEquals(proxiedHello.sayHi("eraser"), "Hi eraser");
            assertEquals(proxiedHello.sayThankYou("eraser"), "Thank You eraser");
        }
    }
}
