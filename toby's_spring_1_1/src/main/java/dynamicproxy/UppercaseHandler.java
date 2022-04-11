package dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

// 모든 오브젝트에 대해 대문자로 수정해주는 핸들러
// invoke 해서 받은 값이 String 이 아닐경우 변환없이 리턴한다
public class UppercaseHandler implements InvocationHandler {
    // 어떤 종류의 타입이던 상관없이 적용할 수 있도록 Object 로 필드를 정의해준다
    Object target;

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 타겟으로 위임, 인터페이스의 모든 메소드 호출에 적용됨
        Object ret = method.invoke(target, args);

        // 리턴타입이 String 일때만 대문자로 수정해준다
        if (ret instanceof String) {
            // 부가기능 제공
            return ((String) ret).toUpperCase();
        } else {
            return ret;
        }


    }
}
