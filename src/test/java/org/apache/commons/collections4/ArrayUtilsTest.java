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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ArrayUtilsTest {

    @Test
    public void testIndexOf() {
        final Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        assertEquals(-1, ArrayUtils.indexOf(null, null));
        assertEquals(-1, ArrayUtils.indexOf(null, "0"));
        assertEquals(-1, ArrayUtils.indexOf(new Object[0], "0"));
        assertEquals(0, ArrayUtils.indexOf(array, "0"));
        assertEquals(1, ArrayUtils.indexOf(array, "1"));
        assertEquals(2, ArrayUtils.indexOf(array, "2"));
        assertEquals(3, ArrayUtils.indexOf(array, "3"));
        assertEquals(4, ArrayUtils.indexOf(array, null));
        assertEquals(-1, ArrayUtils.indexOf(array, "notInArray"));
    }

    @Test
    public void testContains() {
        final Object[] array = new Object[] { "0", "1", "2", "3", null, "0" };
        assertFalse(ArrayUtils.contains(null, null));
        assertFalse(ArrayUtils.contains(null, "1"));
        assertTrue(ArrayUtils.contains(array, "0"));
        assertTrue(ArrayUtils.contains(array, "1"));
        assertTrue(ArrayUtils.contains(array, "2"));
        assertTrue(ArrayUtils.contains(array, "3"));
        assertTrue(ArrayUtils.contains(array, null));
        assertFalse(ArrayUtils.contains(array, "notInArray"));
    }

    @Test
    public void testContains_LANG_1261() {
        class LANG1261ParentObject {
            @Override
            public boolean equals(final Object o) {
                return true;
            }
        }
        class LANG1261ChildObject extends LANG1261ParentObject {
        }
        final Object[] array = new LANG1261ChildObject[] { new LANG1261ChildObject() };
        assertTrue(ArrayUtils.contains(array, new LANG1261ParentObject()));
    }
}
