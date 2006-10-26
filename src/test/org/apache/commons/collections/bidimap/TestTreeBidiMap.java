/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.apache.commons.collections.bidimap;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.BulkTest;

/**
 * JUnit tests.
 * 
 * @version $Revision$ $Date$
 * 
 * @author Stephen Colebourne
 */
public class TestTreeBidiMap extends AbstractTestOrderedBidiMap {

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestTreeBidiMap.class);
    }

    public TestTreeBidiMap(String testName) {
        super(testName);
    }

    public BidiMap makeEmptyBidiMap() {
        return new TreeBidiMap();
    }
    
    public Map makeConfirmedMap() {
        return new TreeMap();
    }

    /**
     * Override to prevent infinite recursion of tests.
     */
    public String[] ignoredTests() {
        return new String[] {"TestTreeBidiMap.bulkTestInverseMap.bulkTestInverseMap"};
    }
    
    public boolean isAllowNullKey() {
        return false;
    }
    
    public boolean isAllowNullValue() {
        return false;
    }
    
    public boolean isSetValueSupported() {
        return false;
    }
    
}
