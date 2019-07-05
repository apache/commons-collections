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
package org.apache.commons.collections4.list;

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LazyListTest extends AbstractObjectTest {

    public LazyListTest(String testName) {
        super(testName);
    }

    @Override
    public Object makeObject() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        return new LazyList<>(new ArrayList<>(), dateFactory);
    }

    @Override
    public void testSimpleSerialization() {
        // Factory and Transformer are not serializable
    }

    @Override
    public void testSerializeDeserializeThenCompare() {
        // Factory and Transformer are not serializable
    }

    @Override
    public void testCanonicalEmptyCollectionExists() {
        // Factory and Transformer are not serializable
    }

    @Override
    public void testCanonicalFullCollectionExists() {
        // Factory and Transformer are not serializable
    }

    public void testElementCreationWithFactory() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        assertTrue(list.isEmpty());

        final LocalDateTime firstElement = list.get(0);
        assertNotNull(firstElement);
        assertFalse(list.isEmpty());
    }

    public void testElementCreationWithTransformer() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        assertTrue(list.isEmpty());

        final LocalDateTime firstElement = list.get(0);
        assertNotNull(firstElement);
        assertFalse(list.isEmpty());
    }

    public void testCreateNullGapsWithFactory() {
        final Factory<LocalDateTime> dateFactory = LocalDateTime::now;
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        final LocalDateTime fourthElement = list.get(3);
        assertFalse(list.isEmpty());
        assertNotNull(fourthElement);
    }

    public void testCreateNullGapsWithTransformer() {
        final List<Integer> hours = Arrays.asList(7, 5, 8, 2);
        final Transformer<Integer, LocalDateTime> dateFactory = input -> LocalDateTime.now().withHour(hours.get(input));
        final List<LocalDateTime> list = new LazyList<>(new ArrayList<>(), dateFactory);

        final LocalDateTime fourthElement = list.get(3);
        assertFalse(list.isEmpty());
        assertNotNull(fourthElement);
    }



}
