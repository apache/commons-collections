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

import org.apache.commons.collections4.map.HashedMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * Test Cases for Map Builder
 */
public class MapBuilderTest {

    @Test
    void setComparator() {
        // Null Comparator
        Map<String, Integer> myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("X", 24);
        myMap.put("B", 2);
        myMap.put("Y", 26);

        // Reverse comparator
        Map<String, Integer> builderMap = new MapBuilder().setData(myMap).setIterationOrder(MapBuilder.KeyOrder.COMPARATOR_ORDER).setComparator(Comparator.reverseOrder()).build();
        Assert.assertEquals(builderMap.keySet().stream().findFirst().get(), "Y");
        Assert.assertEquals(builderMap.keySet().stream().skip(1).findFirst().get(), "X");
        Assert.assertEquals(builderMap.keySet().stream().skip(2).findFirst().get(), "B");
        Assert.assertEquals(builderMap.keySet().stream().skip(3).findFirst().get(), "A");
    }

    @Test
    void setIterationOrder() {
        //Key Order = UNORDERED
        Map<String, Integer> myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("X", 24);
        myMap.put("B", 2);
        myMap.put("Y", 26);
        Map<String, Integer> builderMap = new MapBuilder().setData(myMap).setIterationOrder(MapBuilder.KeyOrder.UNORDERED).build();
        Assert.assertTrue(builderMap instanceof HashedMap);

        //Key Order = INSERTION ORDER
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.INSERTION_ORDER).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertTrue(builderMap instanceof LinkedHashMap);

        //Key Order = NATURAL ORDER
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.NATURAL_ORDER).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertTrue(builderMap instanceof TreeMap);

        //Key Order = COMPARATOR ORDER and null comparator
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.COMPARATOR_ORDER).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertTrue(builderMap instanceof TreeMap);

        //Key Order = COMPARATOR ORDER and valid comparator
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.COMPARATOR_ORDER).setComparator(Comparator.reverseOrder()).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertTrue(builderMap instanceof TreeMap);
    }

    @Test
    void setImmutable() {
        Map<String, Integer> myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("B", 2);
        Map<String, Integer> builderMap = new MapBuilder().setData(myMap).setImmutable(true).build();
        boolean exceptionThrown = false;
        try {
            builderMap.put("C", 3);
        }catch (UnsupportedOperationException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
    }

    @Test
    void setData() {
        Map<String, Integer> myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("B", 2);
        Map<String, Integer> builderMap = new MapBuilder().setData(myMap).build();
        Assert.assertEquals(myMap, builderMap);
    }

    @Test
    void build() {
        Map<String, Integer> builderMap = new MapBuilder().build();
        Assert.assertTrue(builderMap.size() == 0);
    }
}
