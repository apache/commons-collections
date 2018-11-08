package org.apache.commons.collections4.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for COLLECTIONS-701.
 */
public class Collections701Test {

    @Test
    public void testArrayList() {
        final List<Object> list = new ArrayList<>();
        list.add(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(list, list.get(0));
    }

    @Test
    public void testHashSet() {
        final Set<Object> set = new HashSet<>();
        set.add(set);
        Assert.assertEquals(1, set.size());
        Assert.assertEquals(set, set.iterator().next());
    }

    @Test
    @Ignore
    public void testSetUniqueList() {
        final List<Object> source = new ArrayList<>();
        final List<Object> list = SetUniqueList.setUniqueList(source);
        list.add(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(list, list.get(0));
    }
}
