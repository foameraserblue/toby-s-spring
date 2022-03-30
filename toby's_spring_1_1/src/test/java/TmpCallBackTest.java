import com.foameraserblue.tmp.Calculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

public class TmpCallBackTest {
    Calculator calculator;
    String filePath;

    @Before
    public void setUp() {
        calculator = new Calculator();
        filePath = getClass().getResource("numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {

        int sum = calculator.calcSum(filePath);
        Assert.assertEquals(sum, 10);
    }

    @Test
    public void multiplyOfNumbers() throws IOException {

        int multiply = calculator.calcMultiply(filePath);
        Assert.assertEquals(multiply, 24);

    }
}
