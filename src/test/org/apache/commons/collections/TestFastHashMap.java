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
package org.apache.commons.collections;

import java.util.Map;

import junit.framework.Test;

import org.apache.commons.collections.map.AbstractTestMap;

/**
 * Tests FastHashMap.
 * 
 * @version $Revision$ $Date$
 * 
 * @author Jason van Zyl
 */
public class TestFastHashMap extends AbstractTestMap {
    
    public TestFastHashMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestFastHashMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestFastHashMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        FastHashMap fhm = new FastHashMap();
        fhm.setFast(false);
        return (fhm);
    }

}
