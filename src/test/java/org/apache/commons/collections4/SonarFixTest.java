package org.apache.commons.collections4;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SonarFixTest {

    @Test
    public void testCalculate() {
        SonarFix fix = new SonarFix();
        assertEquals(5, fix.calculate(2, 3));
    }
}
