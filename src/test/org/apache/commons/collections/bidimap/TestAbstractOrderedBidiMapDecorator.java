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

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.OrderedBidiMap;

/**
 * Test class for AbstractOrderedBidiMapDecorator.
 *
 * @version $Revision$ $Date$
 */
public class TestAbstractOrderedBidiMapDecorator<K, V>
        extends AbstractTestOrderedBidiMap<K, V> {

    public TestAbstractOrderedBidiMapDecorator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestAbstractOrderedBidiMapDecorator.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedBidiMap<K, V> makeObject() {
        return new TestOrderedBidiMap<K, V>();
    }

    @Override
    public SortedMap<K, V> makeConfirmedMap() {
        return new TreeMap<K, V>();
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
    public boolean isSetValueSupported() {
        return true;
    }

    /**
     * Simple class to actually test.
     */
    private static final class TestOrderedBidiMap<K, V> extends AbstractOrderedBidiMapDecorator<K, V> {

        private TestOrderedBidiMap<V, K> inverse = null;

        public TestOrderedBidiMap() {
            super(new DualTreeBidiMap<K, V>());
        }

        public TestOrderedBidiMap(OrderedBidiMap<K, V> map) {
            super(map);
        }

        @Override
        public OrderedBidiMap<V, K> inverseBidiMap() {
            if (inverse == null) {
                inverse = new TestOrderedBidiMap<V, K>(decorated().inverseBidiMap());
                inverse.inverse = this;
            }
            return inverse;
        }
    }
}
