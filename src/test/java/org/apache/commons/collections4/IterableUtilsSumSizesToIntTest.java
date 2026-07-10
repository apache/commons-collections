/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link IterableUtils#sumSizesToInt(Iterable)}.
 * <p>
 * Uses Mockito to minimize overhead.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class IterableUtilsSumSizesToIntTest {

    @Mock
    private Iterable<? extends Collection<Object>> iterable;

    @Mock
    private Iterator<Collection<Object>> iterator;

    @Mock
    private Collection<Object> coll1;

    @Mock
    private Collection<Object> coll2;

    @Mock
    private Collection<Object> coll3;

    @Test
    void testEmptyIterable() {
        doReturn(iterator).when(iterable).iterator();
        when(iterator.hasNext()).thenReturn(false);
        assertEquals(0, IterableUtils.sumSizesToInt(iterable));
        verify(iterable).iterator();
    }

    @Test
    void testIterableWithNullCollections() {
        doReturn(iterator).when(iterable).iterator();
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(coll1, (Collection<Object>) null);
        when(coll1.size()).thenReturn(5);
        assertEquals(5, IterableUtils.sumSizesToInt(iterable));
    }

    @Test
    void testLargeSizesOverflow() {
        doReturn(iterator).when(iterable).iterator();
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(coll1, coll2);
        when(coll1.size()).thenReturn(Integer.MAX_VALUE);
        when(coll2.size()).thenReturn(10);
        // int overflow behavior capping at Integer.MAX_VALUE.
        assertEquals(Integer.MAX_VALUE, IterableUtils.sumSizesToInt(iterable));
    }

    @Test
    void testMultipleCollections() {
        doReturn(iterator).when(iterable).iterator();
        when(iterator.hasNext()).thenReturn(true, true, true, false);
        when(iterator.next()).thenReturn(coll1, coll2, coll3);
        when(coll1.size()).thenReturn(10);
        when(coll2.size()).thenReturn(20);
        when(coll3.size()).thenReturn(30);
        assertEquals(60, IterableUtils.sumSizesToInt(iterable));
    }

    @Test
    void testNullIterable() {
        assertThrows(NullPointerException.class, () -> IterableUtils.sumSizesToInt(null));
    }

    @Test
    void testOnlyNullCollections() {
        doReturn(iterator).when(iterable).iterator();
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn((Collection<Object>) null);
        assertEquals(0, IterableUtils.sumSizesToInt(iterable));
    }
}
