/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.buffer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Entry point for tests.
 * 
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 * 
 * @author Stephen Colebourne
 */
public class TestAll extends TestCase {
    
    public TestAll(String testName) {
        super(testName);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestAll.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        suite.addTest(TestBoundedFifoBuffer.suite());
        suite.addTest(TestBoundedFifoBuffer2.suite());
        suite.addTest(TestCircularFifoBuffer.suite());
        suite.addTest(TestPriorityBuffer.suite());
        suite.addTest(TestUnboundedFifoBuffer.suite());
        
        suite.addTest(TestBlockingBuffer.suite());
        suite.addTest(TestPredicatedBuffer.suite());
        suite.addTest(TestSynchronizedBuffer.suite());
        suite.addTest(TestTransformedBuffer.suite());
        suite.addTest(TestUnmodifiableBuffer.suite());
        suite.addTest(TestTimeoutBuffer.suite());
        return suite;
    }
        
}
