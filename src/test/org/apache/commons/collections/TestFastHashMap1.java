/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.HashMap;
import java.util.Map;

/**
 * Test FastHashMap in <strong>fast</strong> mode.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: TestFastHashMap1.java,v 1.4.2.1 2004/05/22 12:14:05 scolebourne Exp $
 */
public class TestFastHashMap1 extends TestFastHashMap
{
    public TestFastHashMap1(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return BulkTest.makeSuite(TestFastHashMap1.class);
    }

    public static void main(String args[])
    {
        String[] testCaseName = { TestFastHashMap1.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        FastHashMap fhm = new FastHashMap();
        fhm.setFast(true);
        return (fhm);
    }

    public void setUp()
    {
        map = (HashMap) makeEmptyMap();
    }

}
