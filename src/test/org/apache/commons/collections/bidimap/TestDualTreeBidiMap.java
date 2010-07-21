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
package org.apache.commons.collections.bidimap;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.collections.BulkTest;

/**
 * JUnit tests.
 *
 * @version $Revision$ $Date$
 *
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public class TestDualTreeBidiMap<K extends Comparable<K>, V extends Comparable<V>> extends AbstractTestSortedBidiMap<K, V> {

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestDualTreeBidiMap.class);
    }

    public TestDualTreeBidiMap(String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DualTreeBidiMap<K, V> makeObject() {
        return new DualTreeBidiMap<K, V>();
    }

    /**
     * Override to prevent infinite recursion of tests.
     */
    @Override
    public String[] ignoredTests() {
        return new String[] {"TestDualTreeBidiMap.bulkTestInverseMap.bulkTestInverseMap"};
    }
    
//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/DualTreeBidiMap.emptyCollection.version3.obj");
//        resetFull();
//        writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/DualTreeBidiMap.fullCollection.version3.obj");
//    }
}
