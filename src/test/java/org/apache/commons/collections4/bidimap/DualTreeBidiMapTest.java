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
package org.apache.commons.collections4.bidimap;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;

/**
 * JUnit tests.
 *
 * @version $Id$
 */
public class DualTreeBidiMapTest<K extends Comparable<K>, V extends Comparable<V>> extends AbstractSortedBidiMapTest<K, V> {

    public static Test suite() {
        return BulkTest.makeSuite(DualTreeBidiMapTest.class);
    }

    public DualTreeBidiMapTest(final String testName) {
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
        return new String[] {"DualTreeBidiMapTest.bulkTestInverseMap.bulkTestInverseMap"};
    }
    
//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/DualTreeBidiMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/DualTreeBidiMap.fullCollection.version4.obj");
//    }
}
