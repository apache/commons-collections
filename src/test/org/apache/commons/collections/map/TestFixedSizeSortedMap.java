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

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;

/**
 * Extension of {@link TestSortedMap} for exercising the {@link FixedSizeSortedMap}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.7 $ $Date: 2004/04/09 09:40:15 $
 * 
 * @author Stephen Colebourne
 */
public class TestFixedSizeSortedMap extends AbstractTestSortedMap {

    public TestFixedSizeSortedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestFixedSizeSortedMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestFixedSizeSortedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //-----------------------------------------------------------------------
    public Map makeEmptyMap() {
        return FixedSizeSortedMap.decorate(new TreeMap());
    }

    public Map makeFullMap() {
        SortedMap map = new TreeMap();
        addSampleMappings(map);
        return FixedSizeSortedMap.decorate(map);
    }
    
    public boolean isSubMapViewsSerializable() {
        // TreeMap sub map views have a bug in deserialization.
        return false;
    }

    public boolean isPutAddSupported() {
        return false;
    }

    public boolean isRemoveSupported() {
        return false;
    }

    //-----------------------------------------------------------------------
    public String getCompatibilityVersion() {
        return "3.1";
    }
    
//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/FixedSizeSortedMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/FixedSizeSortedMap.fullCollection.version3.1.obj");
//    }
}
