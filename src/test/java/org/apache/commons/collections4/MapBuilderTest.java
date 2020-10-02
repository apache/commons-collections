package org.apache.commons.collections4;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Cases for Map Builder
 */
class MapBuilderTest {

    @Test
    void setComparator() {
        // Null Comparator
        Map myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("X", 24);
        myMap.put("B", 2);
        myMap.put("Y", 26);
        Map builderMap = new MapBuilder().setData(myMap).setComparator(null).build();
        Assert.assertEquals(myMap, builderMap);

        // Reverse comparator
        builderMap = new MapBuilder().setData(myMap).setIterationOrder(MapBuilder.KeyOrder.COMPARATOR_ORDER).setComparator(Comparator.reverseOrder()).build();
        Assert.assertEquals(builderMap.keySet().stream().findFirst().get(), "Y");
        Assert.assertEquals(builderMap.keySet().stream().skip(1).findFirst().get(), "X");
        Assert.assertEquals(builderMap.keySet().stream().skip(2).findFirst().get(), "B");
        Assert.assertEquals(builderMap.keySet().stream().skip(3).findFirst().get(), "A");
    }

    @Test
    void setIterationOrder() {
        //Key Order = RANDOM
        Map myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("X", 24);
        myMap.put("B", 2);
        myMap.put("Y", 26);
        Map builderMap = new MapBuilder().setData(myMap).setIterationOrder(MapBuilder.KeyOrder.RANDOM).build();
        Assert.assertEquals(myMap, builderMap);

        //Key Order = INSERTION ORDER
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.INSERTION_ORDER).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertEquals(builderMap.keySet().stream().findFirst().get(), "A");
        Assert.assertEquals(builderMap.keySet().stream().skip(1).findFirst().get(), "X");
        Assert.assertEquals(builderMap.keySet().stream().skip(2).findFirst().get(), "B");
        Assert.assertEquals(builderMap.keySet().stream().skip(3).findFirst().get(), "Y");

        //Key Order = NATURAL ORDER
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.NATURAL_ORDER).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertEquals(builderMap.keySet().stream().findFirst().get(), "A");
        Assert.assertEquals(builderMap.keySet().stream().skip(1).findFirst().get(), "B");
        Assert.assertEquals(builderMap.keySet().stream().skip(2).findFirst().get(), "X");
        Assert.assertEquals(builderMap.keySet().stream().skip(3).findFirst().get(), "Y");

        //Key Order = COMPARATOR ORDER and null comparator
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.COMPARATOR_ORDER).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertEquals(builderMap.keySet().stream().findFirst().get(), "A");
        Assert.assertEquals(builderMap.keySet().stream().skip(1).findFirst().get(), "B");
        Assert.assertEquals(builderMap.keySet().stream().skip(2).findFirst().get(), "X");
        Assert.assertEquals(builderMap.keySet().stream().skip(3).findFirst().get(), "Y");

        //Key Order = COMPARATOR ORDER and valid comparator
        builderMap = new MapBuilder().setIterationOrder(MapBuilder.KeyOrder.COMPARATOR_ORDER).setComparator(Comparator.reverseOrder()).build();
        builderMap.put("A", 1);
        builderMap.put("X", 24);
        builderMap.put("B", 2);
        builderMap.put("Y", 26);
        Assert.assertEquals(builderMap.keySet().stream().findFirst().get(), "Y");
        Assert.assertEquals(builderMap.keySet().stream().skip(1).findFirst().get(), "X");
        Assert.assertEquals(builderMap.keySet().stream().skip(2).findFirst().get(), "B");
        Assert.assertEquals(builderMap.keySet().stream().skip(3).findFirst().get(), "A");
    }

    @Test
    void setImmutable() {
        Map myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("B", 2);
        Map builderMap = new MapBuilder().setData(myMap).setImmutable(true).build();
        boolean exceptionThrown = false;
        try {
            builderMap.put("C", 3);
        }catch (UnsupportedOperationException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
        Assert.assertEquals(myMap, builderMap);
    }

    @Test
    void setData() {
        Map myMap = new HashMap();
        myMap.put("A", 1);
        myMap.put("B", 2);
        Map builderMap = new MapBuilder().setData(myMap).build();
        Assert.assertEquals(myMap, builderMap);
    }

    @Test
    void build() {
        Map builderMap = new MapBuilder().build();
        Assert.assertTrue(builderMap.size() == 0);
    }
}