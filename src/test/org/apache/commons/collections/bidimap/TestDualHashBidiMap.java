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
import org.apache.commons.collections.BulkTest;

/**
 * JUnit tests.
 *
 * @version $Revision$ $Date$
 *
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public class TestDualHashBidiMap<K, V> extends AbstractTestBidiMap<K, V> {

    public static Test suite() {
        return BulkTest.makeSuite(TestDualHashBidiMap.class);
    }

    public TestDualHashBidiMap(String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DualHashBidiMap<K, V> makeObject() {
        return new DualHashBidiMap<K, V>();
    }

    /**
     * Override to prevent infinite recursion of tests.
     */
    @Override
    public String[] ignoredTests() {
        return new String[] { "TestDualHashBidiMap.bulkTestInverseMap.bulkTestInverseMap" };
    }
    
//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/DualHashBidiMap.emptyCollection.version3.obj");
//        resetFull();
//        writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/DualHashBidiMap.fullCollection.version3.obj");
//    }
}
