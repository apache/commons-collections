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
package org.apache.commons.collections4;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.set.PredicatedSet;

/**
 * Tests for SetUtils.
 *
 * @version $Id$
 */
public class SetUtilsTest extends BulkTest {

    public SetUtilsTest(final String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(SetUtilsTest.class);
    }

    public void testNothing() {
    }

    public void testpredicatedSet() {
        final Predicate<Object> predicate = new Predicate<Object>() {
            public boolean evaluate(final Object o) {
                return o instanceof String;
            }
        };
        Set<Object> set = SetUtils.predicatedSet(new HashSet<Object>(), predicate);
        assertTrue("returned object should be a PredicatedSet", set instanceof PredicatedSet);
        try {
            set = SetUtils.predicatedSet(new HashSet<Object>(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
        try {
            set = SetUtils.predicatedSet(null, predicate);
            fail("Expecting IllegalArgumentException for null set.");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testEmptyIfNull() {
        assertTrue(SetUtils.emptyIfNull(null).isEmpty());
        
        final Set<Long> set = new HashSet<Long>();
        assertSame(set, SetUtils.emptyIfNull(set));
    }
    
    public void testEquals() {
        final Collection<String> data = Arrays.asList( new String[] { "a", "b", "c" });

        final Set<String> a = new HashSet<String>(data);
        final Set<String> b = new HashSet<String>(data);

        assertEquals(true, a.equals(b));
        assertEquals(true, SetUtils.isEqualSet(a, b));
        a.clear();
        assertEquals(false, SetUtils.isEqualSet(a, b));
        assertEquals(false, SetUtils.isEqualSet(a, null));
        assertEquals(false, SetUtils.isEqualSet(null, b));
        assertEquals(true, SetUtils.isEqualSet(null, null));
    }

    public void testHashCode() {
        final Collection<String> data = Arrays.asList( new String[] { "a", "b", "c" });

        final Set<String> a = new HashSet<String>(data);
        final Set<String> b = new HashSet<String>(data);

        assertEquals(true, a.hashCode() == b.hashCode());
        assertEquals(true, a.hashCode() == SetUtils.hashCodeForSet(a));
        assertEquals(true, b.hashCode() == SetUtils.hashCodeForSet(b));
        assertEquals(true, SetUtils.hashCodeForSet(a) == SetUtils.hashCodeForSet(b));
        a.clear();
        assertEquals(false, SetUtils.hashCodeForSet(a) == SetUtils.hashCodeForSet(b));
        assertEquals(0, SetUtils.hashCodeForSet(null));
    }

}
