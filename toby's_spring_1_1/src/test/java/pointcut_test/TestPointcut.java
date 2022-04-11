package pointcut_test;

import com.foameraserblue.pointcut_test.Bean;
import com.foameraserblue.pointcut_test.Target;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;


public class TestPointcut {

    @Test
    public void methodSignaturePointcut() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int com.foameraserblue.pointcut_test.Target.minus(int,int) " +
                "throws java.lang.RuntimeException)");

        // Target.minus()
        Assert.assertTrue(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null));

        // Target.plus()
        Assert.assertFalse(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null));

        // Bean.method()
        Assert.assertFalse(pointcut.getClassFilter().matches(Bean.class) &&
                pointcut.getMethodMatcher().matches(Bean.class.getMethod("method"), null));

    }



    // 테스트 헬퍼 메소드
    public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        Assert.assertEquals(pointcut.getClassFilter().matches(clazz) &&
                pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null), expected);
    }
}
