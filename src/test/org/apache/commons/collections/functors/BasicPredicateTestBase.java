package org.apache.commons.collections.functors;

import org.junit.Before;

public class BasicPredicateTestBase {
    protected Object cObject;
    protected String cString;
    protected Integer cInteger;

    @Before
    public void initialiseTestObjects() throws Exception {
        cObject = new Object();
        cString = "Hello";
        cInteger = new Integer(6);
    }
}
