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
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;

/**
 * Testcase.
 *
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public class TestObjectGraphIterator extends AbstractTestIterator<Object> {

    protected String[] testArray = { "One", "Two", "Three", "Four", "Five", "Six" };

    protected List<String> list1 = null;
    protected List<String> list2 = null;
    protected List<String> list3 = null;
    protected List<Iterator<String>> iteratorList = null;

    public TestObjectGraphIterator(String testName) {
        super(testName);
    }

    @Override
    public void setUp() {
        list1 = new ArrayList<String>();
        list1.add("One");
        list1.add("Two");
        list1.add("Three");
        list2 = new ArrayList<String>();
        list2.add("Four");
        list3 = new ArrayList<String>();
        list3.add("Five");
        list3.add("Six");
        iteratorList = new ArrayList<Iterator<String>>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
    }

    //-----------------------------------------------------------------------
    @Override
    public ObjectGraphIterator<Object> makeEmptyIterator() {
        ArrayList<Object> list = new ArrayList<Object>();
        return new ObjectGraphIterator<Object>(list.iterator());
    }

    @Override
    public ObjectGraphIterator<Object> makeObject() {
        return new ObjectGraphIterator<Object>(iteratorList.iterator());
    }

    //-----------------------------------------------------------------------
    public void testIteratorConstructor_null1() {
        Iterator<Object> it = new ObjectGraphIterator<Object>(null);

        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {
        }
    }

    public void testIteratorConstructor_null_next() {
        Iterator<Object> it = new ObjectGraphIterator<Object>(null);
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    public void testIteratorConstructor_null_remove() {
        Iterator<Object> it = new ObjectGraphIterator<Object>(null);
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {
        }
    }

    //-----------------------------------------------------------------------
    public void testIteratorConstructorIteration_Empty() {
        List<Iterator<Object>> iteratorList = new ArrayList<Iterator<Object>>();
        Iterator<Object> it = new ObjectGraphIterator<Object>(iteratorList.iterator());

        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {
        }
    }

    public void testIteratorConstructorIteration_Simple() {
        List<Iterator<String>> iteratorList = new ArrayList<Iterator<String>>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        Iterator<Object> it = new ObjectGraphIterator<Object>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertEquals(true, it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    public void testIteratorConstructorIteration_SimpleNoHasNext() {
        List<Iterator<String>> iteratorList = new ArrayList<Iterator<String>>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        Iterator<Object> it = new ObjectGraphIterator<Object>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertEquals(testArray[i], it.next());
        }
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    public void testIteratorConstructorIteration_WithEmptyIterators() {
        List<Iterator<String>> iteratorList = new ArrayList<Iterator<String>>();
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list1.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list3.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        Iterator<Object> it = new ObjectGraphIterator<Object>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertEquals(true, it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    public void testIteratorConstructorRemove() {
        List<Iterator<String>> iteratorList = new ArrayList<Iterator<String>>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        Iterator<Object> it = new ObjectGraphIterator<Object>(iteratorList.iterator());

        for (int i = 0; i < 6; i++) {
            assertEquals(testArray[i], it.next());
            it.remove();
        }
        assertEquals(false, it.hasNext());
        assertEquals(0, list1.size());
        assertEquals(0, list2.size());
        assertEquals(0, list3.size());
    }

    //-----------------------------------------------------------------------
    public void testIteration_IteratorOfIterators() {
        List<Iterator<String>> iteratorList = new ArrayList<Iterator<String>>();
        iteratorList.add(list1.iterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(list3.iterator());
        Iterator<Object> it = new ObjectGraphIterator<Object>(iteratorList.iterator(), null);

        for (int i = 0; i < 6; i++) {
            assertEquals(true, it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertEquals(false, it.hasNext());
    }

    public void testIteration_IteratorOfIteratorsWithEmptyIterators() {
        List<Iterator<String>> iteratorList = new ArrayList<Iterator<String>>();
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list1.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list2.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        iteratorList.add(list3.iterator());
        iteratorList.add(IteratorUtils.<String>emptyIterator());
        Iterator<Object> it = new ObjectGraphIterator<Object>(iteratorList.iterator(), null);

        for (int i = 0; i < 6; i++) {
            assertEquals(true, it.hasNext());
            assertEquals(testArray[i], it.next());
        }
        assertEquals(false, it.hasNext());
    }

    //-----------------------------------------------------------------------
    public void testIteration_RootNull() {
        Iterator<Object> it = new ObjectGraphIterator<Object>(null, null);

        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
        try {
            it.remove();
            fail();
        } catch (IllegalStateException ex) {
        }
    }

    public void testIteration_RootNoTransformer() {
        Forest forest = new Forest();
        Iterator<Object> it = new ObjectGraphIterator<Object>(forest, null);

        assertEquals(true, it.hasNext());
        assertSame(forest, it.next());
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    public void testIteration_Transformed1() {
        Forest forest = new Forest();
        Leaf l1 = forest.addTree().addBranch().addLeaf();
        Iterator<Object> it = new ObjectGraphIterator<Object>(forest, new LeafFinder());

        assertEquals(true, it.hasNext());
        assertSame(l1, it.next());
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    public void testIteration_Transformed2() {
        Forest forest = new Forest();
        forest.addTree();
        forest.addTree();
        forest.addTree();
        Branch b1 = forest.getTree(0).addBranch();
        Branch b2 = forest.getTree(0).addBranch();
        Branch b3 = forest.getTree(2).addBranch();
        /* Branch b4 = */ forest.getTree(2).addBranch();
        Branch b5 = forest.getTree(2).addBranch();
        Leaf l1 = b1.addLeaf();
        Leaf l2 = b1.addLeaf();
        Leaf l3 = b2.addLeaf();
        Leaf l4 = b3.addLeaf();
        Leaf l5 = b5.addLeaf();

        Iterator<Object> it = new ObjectGraphIterator<Object>(forest, new LeafFinder());

        assertEquals(true, it.hasNext());
        assertSame(l1, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l2, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l3, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l4, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l5, it.next());
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    public void testIteration_Transformed3() {
        Forest forest = new Forest();
        forest.addTree();
        forest.addTree();
        forest.addTree();
        Branch b1 = forest.getTree(1).addBranch();
        Branch b2 = forest.getTree(1).addBranch();
        Branch b3 = forest.getTree(2).addBranch();
        Branch b4 = forest.getTree(2).addBranch();
        /* Branch b5 = */ forest.getTree(2).addBranch();
        Leaf l1 = b1.addLeaf();
        Leaf l2 = b1.addLeaf();
        Leaf l3 = b2.addLeaf();
        Leaf l4 = b3.addLeaf();
        Leaf l5 = b4.addLeaf();

        Iterator<Object> it = new ObjectGraphIterator<Object>(forest, new LeafFinder());

        assertEquals(true, it.hasNext());
        assertSame(l1, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l2, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l3, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l4, it.next());
        assertEquals(true, it.hasNext());
        assertSame(l5, it.next());
        assertEquals(false, it.hasNext());
        try {
            it.next();
            fail();
        } catch (NoSuchElementException ex) {
        }
    }

    //-----------------------------------------------------------------------
    static class LeafFinder implements Transformer<Object, Object> {
        public Object transform(Object input) {
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

    //-----------------------------------------------------------------------
    static class Forest {
        List<Tree> trees = new ArrayList<Tree>();

        Tree addTree() {
            trees.add(new Tree());
            return getTree(trees.size() - 1);
        }

        Tree getTree(int index) {
            return trees.get(index);
        }

        Iterator<Tree> treeIterator() {
            return trees.iterator();
        }
    }

    static class Tree {
        List<Branch> branches = new ArrayList<Branch>();

        Branch addBranch() {
            branches.add(new Branch());
            return getBranch(branches.size() - 1);
        }

        Branch getBranch(int index) {
            return branches.get(index);
        }

        Iterator<Branch> branchIterator() {
            return branches.iterator();
        }
    }

    static class Branch {
        List<Leaf> leaves = new ArrayList<Leaf>();

        Leaf addLeaf() {
            leaves.add(new Leaf());
            return getLeaf(leaves.size() - 1);
        }

        Leaf getLeaf(int index) {
            return leaves.get(index);
        }

        Iterator<Leaf> leafIterator() {
            return leaves.iterator();
        }
    }

    static class Leaf {
        String colour;

        String getColour() {
            return colour;
        }

        void setColour(String colour) {
            this.colour = colour;
        }
    }

}
