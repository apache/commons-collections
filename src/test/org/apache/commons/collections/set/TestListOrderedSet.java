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
package org.apache.commons.collections.set;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Extension of {@link TestSet} for exercising the {@link ListOrderedSet}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Henning P. Schmiedehausen
 * @author Stephen Colebourne
 */
public class TestListOrderedSet<E> extends AbstractTestSet<E> {

    private static final Integer ZERO = new Integer(0);
    private static final Integer ONE = new Integer(1);
    private static final Integer TWO = new Integer(2);
    private static final Integer THREE = new Integer(3);

    public TestListOrderedSet(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestListOrderedSet.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestListOrderedSet.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public ListOrderedSet<E> makeObject() {
        return ListOrderedSet.decorate(new HashSet<E>());
    }

    @SuppressWarnings("unchecked")
    protected ListOrderedSet<E> setupSet() {
        ListOrderedSet<E> set = makeObject();

        for (int i = 0; i < 10; i++) {
            set.add((E) Integer.toString(i));
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    public void testOrdering() {
        ListOrderedSet<E> set = setupSet();
        Iterator<E> it = set.iterator();

        for (int i = 0; i < 10; i++) {
            assertEquals("Sequence is wrong", Integer.toString(i), it.next());
        }

        for (int i = 0; i < 10; i += 2) {
            assertTrue("Must be able to remove int", set.remove(Integer.toString(i)));
        }

        it = set.iterator();
        for (int i = 1; i < 10; i += 2) {
            assertEquals("Sequence is wrong after remove ", Integer.toString(i), it.next());
        }

        for (int i = 0; i < 10; i++) {
            set.add((E) Integer.toString(i));
        }

        assertEquals("Size of set is wrong!", 10, set.size());

        it = set.iterator();
        for (int i = 1; i < 10; i += 2) {
            assertEquals("Sequence is wrong", Integer.toString(i), it.next());
        }
        for (int i = 0; i < 10; i += 2) {
            assertEquals("Sequence is wrong", Integer.toString(i), it.next());
        }
    }

    @SuppressWarnings("unchecked")
    public void testListAddRemove() {
        ListOrderedSet<E> set = makeObject();
        List<E> view = set.asList();
        set.add((E) ZERO);
        set.add((E) ONE);
        set.add((E) TWO);

        assertEquals(3, set.size());
        assertSame(ZERO, set.get(0));
        assertSame(ONE, set.get(1));
        assertSame(TWO, set.get(2));
        assertEquals(3, view.size());
        assertSame(ZERO, view.get(0));
        assertSame(ONE, view.get(1));
        assertSame(TWO, view.get(2));

        assertEquals(0, set.indexOf(ZERO));
        assertEquals(1, set.indexOf(ONE));
        assertEquals(2, set.indexOf(TWO));

        set.remove(1);
        assertEquals(2, set.size());
        assertSame(ZERO, set.get(0));
        assertSame(TWO, set.get(1));
        assertEquals(2, view.size());
        assertSame(ZERO, view.get(0));
        assertSame(TWO, view.get(1));
    }

    @SuppressWarnings("unchecked")
    public void testListAddIndexed() {
        ListOrderedSet<E> set = makeObject();
        set.add((E) ZERO);
        set.add((E) TWO);

        set.add(1, (E) ONE);
        assertEquals(3, set.size());
        assertSame(ZERO, set.get(0));
        assertSame(ONE, set.get(1));
        assertSame(TWO, set.get(2));

        set.add(0, (E) ONE);
        assertEquals(3, set.size());
        assertSame(ZERO, set.get(0));
        assertSame(ONE, set.get(1));
        assertSame(TWO, set.get(2));

        List<E> list = new ArrayList<E>();
        list.add((E) ZERO);
        list.add((E) TWO);

        set.addAll(0, list);
        assertEquals(3, set.size());
        assertSame(ZERO, set.get(0));
        assertSame(ONE, set.get(1));
        assertSame(TWO, set.get(2));

        list.add(0, (E) THREE); // list = [3,0,2]
        set.remove(TWO);    //  set = [0,1]
        set.addAll(1, list);
        assertEquals(4, set.size());
        assertSame(ZERO, set.get(0));
        assertSame(THREE, set.get(1));
        assertSame(TWO, set.get(2));
        assertSame(ONE, set.get(3));
    }

    @SuppressWarnings("unchecked")
    public void testListAddReplacing() {
        ListOrderedSet<E> set = makeObject();
        A a = new A();
        B b = new B();
        set.add((E) a);
        assertEquals(1, set.size());
        set.add((E) b);  // will match but not replace A as equal
        assertEquals(1, set.size());
        assertSame(a, set.decorated().iterator().next());
        assertSame(a, set.iterator().next());
        assertSame(a, set.get(0));
        assertSame(a, set.asList().get(0));
    }

    static class A {
        public boolean equals(Object obj) {
            return (obj instanceof A || obj instanceof B);
        }
        public int hashCode() {
            return 1;
        }
    }

    static class B {
        public boolean equals(Object obj) {
            return (obj instanceof A || obj instanceof B);
        }
        public int hashCode() {
            return 1;
        }
    }

    public void testDecorator() {
        try {
            ListOrderedSet.decorate((List<E>) null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ListOrderedSet.decorate((Set<E>) null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ListOrderedSet.decorate(null, null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ListOrderedSet.decorate(new HashSet<E>(), null);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            ListOrderedSet.decorate(null, new ArrayList<E>());
            fail();
        } catch (IllegalArgumentException ex) {}
    }

    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/ListOrderedSet.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/ListOrderedSet.fullCollection.version3.1.obj");
//    }

}
