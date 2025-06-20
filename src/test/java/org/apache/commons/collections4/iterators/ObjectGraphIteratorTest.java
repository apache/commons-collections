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
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Transformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testcase.
 */
class ObjectGraphIteratorTest extends AbstractIteratorTest<Object> {

    static class Branch {

        List<Leaf> leaves = new ArrayList<>();

        Leaf addLeaf() {
            leaves.add(new Leaf());
            return getLeaf(leaves.size() - 1);
        }

        Leaf getLeaf(final int index) {
            return leaves.get(index);
        }

        Iterator<Leaf> leafIterator() {
            return leaves.iterator();
        }

    }

    static class Forest {

        List<Tree> trees = new ArrayList<>();

        Tree addTree() {
            trees.add(new Tree());
            return getTree(trees.size() - 1);
        }

        Tree getTree(final int index) {
            return trees.get(index);
        }

        Iterator<Tree> treeIterator() {
            return trees.iterator();
        }

    }
    static class Leaf {

        String color;

        String getColor() {
            return color;
        }

        void setColor(final String color) {
            this.color = color;
        }

    }
    static class LeafFinder implements Transformer<Object, Object> {

        @Override
        public Object transform(final Object input) {
            if (input instanceof Forest) {
                return ((Forest) input).treeIterator();
            }
            if (input instanceof Tree) {
                return ((Tree) input).branchIterator();
            }
            if (input instanceof Branch) {
                return ((Branch) input).leafIterator();
            }
            if (input instanceof Leaf) {
                return input;
            }
            throw new ClassCastException();
        }

    }
    static class Tree {

        List<Branch> branches = new ArrayList<>();

        Branch addBranch() {
            branches.add(new Branch());
            return getBranch(branches.size() - 1);
        }

        Iterator<Branch> branchIterator() {
            return branches.iterator();
        }

        Branch getBranch(final int index) {
            return branches.get(index);
        }

    }

    protected String[] testArray = { "One", "Two", "Three", "Four", "Five", "Six" };

    protected List<String> list1;

    protected List<String> list2;

    protected List<String> list3;

    protected List<Iterator<String>> iteratorList;

    @Override
    public ObjectGraphIterator<Object> makeEmptyIterator() {
        final ArrayList<Object> list = new ArrayList<>();
        return new ObjectGraphIterator<>(list.iterator());
    }

    @Override
    public ObjectGraphIterator<Object> makeObject() {
        setUp();
        return new ObjectGraphIterator<>(iteratorList.iterator());
    }

    @BeforeEach
    public void setUp() {
        list1 = new ArrayList<>();
        list1.add("One");
        list1.add("Two");
        list1.add("Three");
        list2 = new ArrayList<>();
        list2.add("Four");
        list3 = new ArrayList<>();
        list3.add("Five");
        list3.add("Six");
        iteratorList = new ArrayList<>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
    }

    @Test
    void testIteration_IteratorOfIterators() {
        final List<Iterator<String>> iteratorList = new ArrayList<>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        final Iterator<Object> it = new ObjectGraphIterator<>(iteratorList.iterator(), null);

        for (int i = 0; i < 6; i++) {
            assertTrue(it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertFalse(it.hasNext());
    }

    @Test
    void testIteration_IteratorOfIteratorsWithEmptyIterators() {
        final List<Iterator<String>> iteratorList = new ArrayList<>();
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list1.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list3.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        final Iterator<Object> it = new ObjectGraphIterator<>(iteratorList.iterator(), null);

        for (int i = 0; i < 6; i++) {
            assertTrue(it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertFalse(it.hasNext());
    }

    @Test
    void testIteration_RootNoTransformer() {
        final Forest forest = new Forest();
        final Iterator<Object> it = new ObjectGraphIterator<>(forest, null);

        assertTrue(it.hasNext());
        assertSame(forest, it.next());
        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteration_RootNull() {
        final Iterator<Object> it = new ObjectGraphIterator<>(null, null);

        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());

        assertThrows(IllegalStateException.class, () -> it.remove());
    }

    @Test
    void testIteration_Transformed1() {
        final Forest forest = new Forest();
        final Leaf l1 = forest.addTree().addBranch().addLeaf();
        final Iterator<Object> it = new ObjectGraphIterator<>(forest, new LeafFinder());

        assertTrue(it.hasNext());
        assertSame(l1, it.next());
        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteration_Transformed2() {
        final Forest forest = new Forest();
        forest.addTree();
        forest.addTree();
        forest.addTree();
        final Branch b1 = forest.getTree(0).addBranch();
        final Branch b2 = forest.getTree(0).addBranch();
        final Branch b3 = forest.getTree(2).addBranch();
        /* Branch b4 = */ forest.getTree(2).addBranch();
        final Branch b5 = forest.getTree(2).addBranch();
        final Leaf l1 = b1.addLeaf();
        final Leaf l2 = b1.addLeaf();
        final Leaf l3 = b2.addLeaf();
        final Leaf l4 = b3.addLeaf();
        final Leaf l5 = b5.addLeaf();

        final Iterator<Object> it = new ObjectGraphIterator<>(forest, new LeafFinder());

        assertTrue(it.hasNext());
        assertSame(l1, it.next());
        assertTrue(it.hasNext());
        assertSame(l2, it.next());
        assertTrue(it.hasNext());
        assertSame(l3, it.next());
        assertTrue(it.hasNext());
        assertSame(l4, it.next());
        assertTrue(it.hasNext());
        assertSame(l5, it.next());
        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteration_Transformed3() {
        final Forest forest = new Forest();
        forest.addTree();
        forest.addTree();
        forest.addTree();
        final Branch b1 = forest.getTree(1).addBranch();
        final Branch b2 = forest.getTree(1).addBranch();
        final Branch b3 = forest.getTree(2).addBranch();
        final Branch b4 = forest.getTree(2).addBranch();
        /* Branch b5 = */ forest.getTree(2).addBranch();
        final Leaf l1 = b1.addLeaf();
        final Leaf l2 = b1.addLeaf();
        final Leaf l3 = b2.addLeaf();
        final Leaf l4 = b3.addLeaf();
        final Leaf l5 = b4.addLeaf();

        final Iterator<Object> it = new ObjectGraphIterator<>(forest, new LeafFinder());

        assertTrue(it.hasNext());
        assertSame(l1, it.next());
        assertTrue(it.hasNext());
        assertSame(l2, it.next());
        assertTrue(it.hasNext());
        assertSame(l3, it.next());
        assertTrue(it.hasNext());
        assertSame(l4, it.next());
        assertTrue(it.hasNext());
        assertSame(l5, it.next());
        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteratorConstructor_null_next() {
        final Iterator<Object> it = new ObjectGraphIterator<>(null);
        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteratorConstructor_null_remove() {
        final Iterator<Object> it = new ObjectGraphIterator<>(null);
        assertThrows(IllegalStateException.class, () -> it.remove());
    }

    @Test
    void testIteratorConstructor_null1() {
        final Iterator<Object> it = new ObjectGraphIterator<>(null);

        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());

        assertThrows(IllegalStateException.class, () -> it.remove());
    }

    @Test
    void testIteratorConstructorIteration_Empty() {
        final List<Iterator<Object>> iteratorList = new ArrayList<>();
        final Iterator<Object> it = new ObjectGraphIterator<>(iteratorList.iterator());

        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());

        assertThrows(IllegalStateException.class, () -> it.remove());
    }

    @Test
    void testIteratorConstructorIteration_Simple() {
        final List<Iterator<String>> iteratorList = new ArrayList<>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        final Iterator<Object> it = new ObjectGraphIterator<>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertTrue(it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteratorConstructorIteration_SimpleNoHasNext() {
        final List<Iterator<String>> iteratorList = new ArrayList<>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        final Iterator<Object> it = new ObjectGraphIterator<>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertEquals(testArray[i], it.next());
        }

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteratorConstructorIteration_WithEmptyIterators() {
        final List<Iterator<String>> iteratorList = new ArrayList<>();
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list1.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list3.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        final Iterator<Object> it = new ObjectGraphIterator<>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertTrue(it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    void testIteratorConstructorRemove() {
        final List<Iterator<String>> iteratorList = new ArrayList<>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        final Iterator<Object> it = new ObjectGraphIterator<>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertEquals(testArray[i], it.next());
            it.remove();
        }
        assertFalse(it.hasNext());
        assertEquals(0, list1.size());
        assertEquals(0, list2.size());
        assertEquals(0, list3.size());
    }

}
