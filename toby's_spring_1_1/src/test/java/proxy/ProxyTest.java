package proxy;

import dynamicproxy.Hello;
import dynamicproxy.HelloTarget;
import dynamicproxy.UppercaseAdvice;
import dynamicproxy.UppercaseHandler;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

public class ProxyTest {

    @Test
    public void proxyTest() {
        // 프록시팩토리에서 다이내믹 프록시 오브젝트 생성
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                // 동적으로 생성되는 다이내믹 프록시 클래스 로딩에 사용할 클래스로더
                getClass().getClassLoader(),
                // 구현할 인터페이스
                new Class[]{Hello.class},
                // 부가기능과 위임을 담은 핸들러(부가기능)와 여기선 마지막 타겟(위임)
                new UppercaseHandler(new HelloTarget())
        );

        System.out.println(proxiedHello.sayHello("지우개"));
    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello helloProxy = (Hello) pfBean.getObject();

        Assert.assertEquals(helloProxy.sayHello("eraser"), "HELLO ERASER");
        Assert.assertEquals(helloProxy.sayHi("eraser"), "HI ERASER");
        Assert.assertEquals(helloProxy.sayThankYou("eraser"), "THANK YOU ERASER");
    }

    // 포인트컷과 어드바이스를 적용하는 학습 테스트
    // 어드바이저 = 포인트컷(메소드 선정 알고리즘) + 어드바이스(부가기능)
    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        // 메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        // 포인트컷과 어드바이스를 advisor 로 묶어 한 번에 추가
        // 프록시에는 여러개의 어드바이스랑 포인트컷이 추가될 수 있는데
        // 이렇게 묶어서 적용해야 어떤 어드바이스에 어떤 포인트컷을 설정할것인지 확실하기 때문임
        // 이로 인해 여러개의 어드바이스가 등록 되더라도 각각 다른 포인트컷과 조합이 가능하다
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut,new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        Assert.assertEquals(proxiedHello.sayHello("eraser"), "HELLO ERASER");
        Assert.assertEquals(proxiedHello.sayHi("eraser"), "HI ERASER");
        Assert.assertEquals(proxiedHello.sayThankYou("eraser"), "Thank You eraser");
    }
}
