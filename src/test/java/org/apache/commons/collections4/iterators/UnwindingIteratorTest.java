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
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UnwindingIteratorTest {

    @Test
    public void simpleTest() {
        List<Iterator<String>> lst = new ArrayList<>();
        lst.add(Arrays.asList("Hello", "World").iterator());
        lst.add(Arrays.asList("it", "is", "good", "to", "be", "here").iterator());
        List<String> expected = Arrays.asList("Hello", "World", "it", "is", "good", "to", "be", "here");

        Iterator<String> iter = new UnwindingIterator<>(lst.iterator());
        List<String> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(actual, expected);
    }

    @Test
    public void mixedTest() {
        List<List<Number>> lst = new ArrayList<>();

        List<Integer> iList = Arrays.asList(1, 3);
        lst.add(Arrays.asList(3.14f, Math.sqrt(2.0)));

        Iterator<Iterator<Number>> toBeUnwound = new Iterator<Iterator<Number>>() {
            List<List<Number>> lst = Arrays.asList(
                    Arrays.asList(1, 3),
                    Arrays.asList(3.14F, Math.sqrt(2.0))
            );
            Iterator<List<Number>> lstIter = lst.iterator();

            @Override
            public boolean hasNext() {
                return lstIter.hasNext();
            }

            @Override
            public Iterator<Number> next() {
                return lstIter.next().iterator();
            }
        };

        List<Number> expected = Arrays.asList(1, 3, 3.14f, Math.sqrt(2.0));

        Iterator<Number> iter = new UnwindingIterator<>(toBeUnwound);
        List<Number> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(actual, expected);
    }

}
