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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests for COLLECTIONS-701.
 */
public class Collections701Test {

    @Test
    public void testArrayList() {
        final List<Object> list = new ArrayList<>();
        list.add(list);
        assertEquals(1, list.size());
        assertEquals(list, list.get(0));
    }

    @Test
    public void testHashSet() {
        final Set<Object> set = new HashSet<>();
        set.add(set);
        assertEquals(1, set.size());
        assertEquals(set, set.iterator().next());
    }

    @Test
    public void testSetUniqueList() {
        final List<Object> source = new ArrayList<>();
        final List<Object> list = SetUniqueList.setUniqueList(source);
        list.add(list);
        assertEquals(1, list.size());
        assertEquals(list, list.get(0));
    }
}
