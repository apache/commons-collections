/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;


/**
 *  Tests for MapUtils.
 */
public class TestMapUtils extends BulkTest {

    public TestMapUtils(String name) {
        super(name);
    }


    public static Test suite() {
        return BulkTest.makeSuite(TestMapUtils.class);
    }

    public Predicate getPredicate() {
        return new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
    }


    public void testPredicatedMapIllegalPut() {
        Predicate p = getPredicate();
        Map map = MapUtils.predicatedMap(new HashMap(), p, p);
        try {
            map.put("Hi", new Integer(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            map.put(new Integer(3), "Hi");
            fail("Illegal key should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        assertTrue(!map.containsKey(new Integer(3)));
        assertTrue(!map.containsValue(new Integer(3)));

        Map map2 = new HashMap();
        map2.put("A", "a");
        map2.put("B", "b");
        map2.put("C", "c");
        map2.put("c", new Integer(3));

        try {
            map.putAll(map2);
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }

        map.put("E", "e");
        Iterator iterator = map.entrySet().iterator();
        try {
            Map.Entry entry = (Map.Entry)iterator.next();
            entry.setValue(new Integer(3));
            fail("Illegal value should raise IllegalArgument");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }


    public BulkTest bulkTestPredicatedMap() {
        return new TestMap("") {
            public boolean useNullKey() {
                return false;
            }

            public boolean useNullValue() {
                return false;
            }

            public Map makeEmptyMap() {
                Predicate p = getPredicate();
                return MapUtils.predicatedMap(new HashMap(), p, p);
            }
        };
    }


}
