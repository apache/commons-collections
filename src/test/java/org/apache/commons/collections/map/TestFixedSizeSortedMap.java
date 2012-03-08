/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.map;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;

/**
 * Extension of {@link AbstractTestSortedMap} for exercising the {@link FixedSizeSortedMap}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class TestFixedSizeSortedMap<K, V> extends AbstractTestSortedMap<K, V> {

    public TestFixedSizeSortedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestFixedSizeSortedMap.class);
    }

    //-----------------------------------------------------------------------
    @Override
    public SortedMap<K, V> makeObject() {
        return FixedSizeSortedMap.fixedSizeSortedMap(new TreeMap<K, V>());
    }

    @Override
    public SortedMap<K, V> makeFullMap() {
        SortedMap<K, V> map = new TreeMap<K, V>();
        addSampleMappings(map);
        return FixedSizeSortedMap.fixedSizeSortedMap(map);
    }

    @Override
    public boolean isSubMapViewsSerializable() {
        // TreeMap sub map views have a bug in deserialization.
        return false;
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    //-----------------------------------------------------------------------
    @Override
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
