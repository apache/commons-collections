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
package org.apache.commons.collections4.map;

import org.apache.commons.collections4.IterableSortedMap;
import org.apache.commons.collections4.OrderedMap;
import org.junit.jupiter.api.Nested;

import java.util.SortedMap;

/**
 * Abstract test class to apply tests for both {@link SortedMap} and {@link OrderedMap} contracts.
 */
public abstract class AbstractIterableSortedMapTest<K, V> extends AbstractMapTest<K, V> {
    protected AbstractIterableSortedMapTest(final String testName) {
        super(testName);
    }

    @Override
    public abstract IterableSortedMap<K, V> makeObject();

    @Nested
    public class TestAsSortedMap extends AbstractSortedMapTest<K, V> {
        public TestAsSortedMap() {
            super("TestAsSortedMap");
        }

        @Override
        public SortedMap<K, V> makeObject() {
            return AbstractIterableSortedMapTest.this.makeObject();
        }
    }

    @Nested
    public class TestAsOrderedMap extends AbstractOrderedMapTest<K, V> {
        public TestAsOrderedMap() {
            super("TestAsOrderedMap");
        }

        @Override
        public OrderedMap<K, V> makeObject() {
            return AbstractIterableSortedMapTest.this.makeObject();
        }
    }
}
