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
package org.apache.commons.collections.map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Entry point for tests.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.15 $ $Date: 2004/04/09 14:46:35 $
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
        
        suite.addTest(TestCaseInsensitiveMap.suite());
        suite.addTest(TestCompositeMap.suite());
        suite.addTest(TestFlat3Map.suite());
        suite.addTest(TestHashedMap.suite());
        suite.addTest(TestIdentityMap.suite());
        suite.addTest(TestLinkedMap.suite());
        suite.addTest(TestLRUMap.suite());
        suite.addTest(TestReferenceMap.suite());
        suite.addTest(TestStaticBucketMap.suite());
        
        suite.addTest(TestFixedSizeMap.suite());
        suite.addTest(TestFixedSizeSortedMap.suite());
        suite.addTest(TestLazyMap.suite());
        suite.addTest(TestLazySortedMap.suite());
        suite.addTest(TestListOrderedMap.suite());
        suite.addTest(TestTransformedMap.suite());
        suite.addTest(TestTransformedSortedMap.suite());
        suite.addTest(TestPredicatedMap.suite());
        suite.addTest(TestPredicatedSortedMap.suite());
        suite.addTest(TestSingletonMap.suite());
        suite.addTest(TestUnmodifiableMap.suite());
        suite.addTest(TestUnmodifiableOrderedMap.suite());
        suite.addTest(TestUnmodifiableSortedMap.suite());
        
        return suite;
    }
        
}
