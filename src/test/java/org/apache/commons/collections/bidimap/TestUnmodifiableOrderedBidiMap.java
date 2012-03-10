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

import java.util.Map;
import java.util.TreeMap;

import junit.framework.Test;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.OrderedBidiMap;

/**
 * JUnit tests.
 *
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class TestUnmodifiableOrderedBidiMap<K extends Comparable<K>, V extends Comparable<V>> extends AbstractTestOrderedBidiMap<K, V> {

    public static Test suite() {
        return BulkTest.makeSuite(TestUnmodifiableOrderedBidiMap.class);
    }

    public TestUnmodifiableOrderedBidiMap(String testName) {
        super(testName);
    }

    @Override
    public OrderedBidiMap<K, V> makeObject() {
        return UnmodifiableOrderedBidiMap.unmodifiableOrderedBidiMap(new TreeBidiMap<K, V>());
    }

    @Override
    public BidiMap<K, V> makeFullMap() {
        OrderedBidiMap<K, V> bidi = new TreeBidiMap<K, V>();
        addSampleMappings(bidi);
        return UnmodifiableOrderedBidiMap.unmodifiableOrderedBidiMap(bidi);
    }

    @Override
    public Map<K, V> makeConfirmedMap() {
        return new TreeMap<K, V>();
    }

    /**
     * Override to prevent infinite recursion of tests.
     */
    @Override
    public String[] ignoredTests() {
        return new String[] {"TestUnmodifiableOrderedBidiMap.bulkTestInverseMap.bulkTestInverseMap"};
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValue() {
        return false;
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isPutChangeSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

}
