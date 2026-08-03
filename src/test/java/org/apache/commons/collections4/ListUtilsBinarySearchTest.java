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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * Unit tests {@link ListUtils} binarySearch functions.
 */
public class ListUtilsBinarySearchTest {

    @Test
    public void binarySearchFirst_whenLowHigherThanEnd_throw() {
        final List<Data> list = createList(0, 1);
        assertThrowsExactly(IllegalArgumentException.class, () ->
                ListUtils.binarySearchFirst(list, 1, 0, 0, Data::getValue, Integer::compare));
    }

    @Test
    public void binarySearchFirst_whenLowNegative_throw() {
        final List<Data> list = createList(0, 1);
        assertThrowsExactly(ArrayIndexOutOfBoundsException.class, () ->
                ListUtils.binarySearchFirst(list, -1, 0, 0, Data::getValue, Integer::compare));
    }

    @Test
    public void binarySearchFirst_whenEndBeyondLength_throw() {
        final List<Data> list = createList(0, 1);
        assertThrowsExactly(ArrayIndexOutOfBoundsException.class, () ->
                ListUtils.binarySearchFirst(list, 0, 3, 0, Data::getValue, Integer::compare));
    }

    @Test
    public void binarySearchLast_whenLowHigherThanEnd_throw() {
        final List<Data> list = createList(0, 1);
        assertThrowsExactly(IllegalArgumentException.class, () ->
                ListUtils.binarySearchLast(list, 1, 0, 0, Data::getValue, Integer::compare));
    }

    @Test
    public void binarySearchFirst_whenEmpty_returnM1() {
        final List<Data> list = createList();
        final int found = ListUtils.binarySearchFirst(list, 0, Data::getValue, Integer::compare);
        assertEquals(-1, found);
    }

    @Test
    public void binarySearchFirst_whenExists_returnIndex() {
        final List<Data> list = createList(0, 1, 2, 4, 7, 9, 12, 15, 17, 19, 25);
        final int found = ListUtils.binarySearchFirst(list, 9, Data::getValue, Integer::compare);
        assertEquals(5, found);
    }

    @Test
    @Timeout(10)
    public void binarySearchFirst_whenMultiple_returnFirst() {
        final List<Data> list = createList(3, 4, 6, 6, 6, 7, 7, 8, 8, 9, 9, 9);
        for (int i = 0; i < list.size(); ++i) {
            if (i > 0 && list.get(i).value == list.get(i - 1).value) {
                continue;
            }
            final int found = ListUtils.binarySearchFirst(list, list.get(i).value, Data::getValue, Integer::compare);
            assertEquals(i, found);
        }
    }

    @Test
    @Timeout(10)
    public void binarySearchLast_whenMultiple_returnFirst() {
        final List<Data> list = createList(3, 4, 6, 6, 6, 7, 7, 8, 8, 9, 9, 9);
        for (int i = 0; i < list.size(); ++i) {
            if (i < list.size() - 1 && list.get(i).value == list.get(i + 1).value) {
                continue;
            }
            final int found = ListUtils.binarySearchLast(list, list.get(i).value, Data::getValue, Integer::compare);
            assertEquals(i, found);
        }
    }

    @Test
    public void binarySearchFirst_whenNotExistsMiddle_returnMinusInsertion() {
        final List<Data> list = createList(0, 1, 2, 4, 7, 9, 12, 15, 17, 19, 25);
        final int found = ListUtils.binarySearchFirst(list, 8, Data::getValue, Integer::compare);
        assertEquals(-6, found);
    }

    @Test
    public void binarySearchFirst_whenNotExistsBeginning_returnMinus1() {
        final List<Data> list = createList(0, 1, 2, 4, 7, 9, 12, 15, 17, 19, 25);
        final int found = ListUtils.binarySearchFirst(list, -3, Data::getValue, Integer::compare);
        assertEquals(-1, found);
    }

    @Test
    public void binarySearchFirst_whenNotExistsEnd_returnMinusLength() {
        final List<Data> list = createList(0, 1, 2, 4, 7, 9, 12, 15, 17, 19, 25);
        final int found = ListUtils.binarySearchFirst(list, 29, Data::getValue, Integer::compare);
        assertEquals(-(list.size() + 1), found);
    }

    @Test
    @Timeout(10)
    public void binarySearchFirst_whenUnsorted_dontInfiniteLoop() {
        final List<Data> list = createList(7, 1, 4, 9, 11, 8);
        final int found = ListUtils.binarySearchFirst(list, 10, Data::getValue, Integer::compare);
    }

    private List<Data> createList(int... values) {
        return IntStream.of(values).mapToObj(Data::new)
                .collect(Collectors.toList());
    }

    static class Data {

        private final int value;

        Data(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
