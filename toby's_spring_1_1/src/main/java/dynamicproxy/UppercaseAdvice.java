package dynamicproxy;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class UppercaseAdvice implements MethodInterceptor {

    // 어드바이스가 일종의 템플릿이고, MethodInvocation 이 콜백역할을 하여
    // 어드바이스는 단순 부가기능을 추가하는 역할만 할 뿐 타겟의 직접적인 정보를 알지 않아도됨
    // 즉 필드변수를 가질 필요가 없기때문에 싱글톤 빈 형태로 여러 객체가 공유하여 사용할 수 있음
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String ret = (String) methodInvocation.proceed();
        return ret.toUpperCase();
    }
}
