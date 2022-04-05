package proxy;

import dynamicproxy.Hello;
import dynamicproxy.HelloTarget;
import dynamicproxy.UppercaseHandler;
import org.junit.Test;

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
}
