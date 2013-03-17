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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.set.CompositeSet.SetMutator;

/**
 * Extension of {@link AbstractSetTest} for exercising the
 * {@link CompositeSet} implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class CompositeSetTest<E> extends AbstractSetTest<E> {
    public CompositeSetTest(final String name) {
        super(name);
    }

    @Override
    public CompositeSet<E> makeObject() {
        final HashSet<E> contained = new HashSet<E>();
        final CompositeSet<E> set = new CompositeSet<E>(contained);
        set.setMutator( new EmptySetMutator<E>(contained) );
        return set;
    }

    @SuppressWarnings("unchecked")
    public Set<E> buildOne() {
        final HashSet<E> set = new HashSet<E>();
        set.add((E) "1");
        set.add((E) "2");
        return set;
    }

    @SuppressWarnings("unchecked")
    public Set<E> buildTwo() {
        final HashSet<E> set = new HashSet<E>();
        set.add((E) "3");
        set.add((E) "4");
        return set;
    }

    @SuppressWarnings("unchecked")
    public void testContains() {
        final CompositeSet<E> set = new CompositeSet<E>(new Set[]{ buildOne(), buildTwo() });
        assertTrue(set.contains("1"));
    }

    @SuppressWarnings("unchecked")
    public void testRemoveUnderlying() {
        final Set<E> one = buildOne();
        final Set<E> two = buildTwo();
        final CompositeSet<E> set = new CompositeSet<E>(new Set[] { one, two });
        one.remove("1");
        assertFalse(set.contains("1"));

        two.remove("3");
        assertFalse(set.contains("3"));
    }

    @SuppressWarnings("unchecked")
    public void testRemoveComposited() {
        final Set<E> one = buildOne();
        final Set<E> two = buildTwo();
        final CompositeSet<E> set = new CompositeSet<E>(new Set[] { one, two });
        set.remove("1");
        assertFalse(one.contains("1"));

        set.remove("3");
        assertFalse(one.contains("3"));
    }

    @SuppressWarnings("unchecked")
    public void testFailedCollisionResolution() {
        final Set<E> one = buildOne();
        final Set<E> two = buildTwo();
        final CompositeSet<E> set = new CompositeSet<E>(new Set[] { one, two });
        set.setMutator(new SetMutator<E>() {
            private static final long serialVersionUID = 1L;

            public void resolveCollision(final CompositeSet<E> comp, final Set<E> existing,
                final Set<E> added, final Collection<E> intersects) {
                //noop
            }

            public boolean add(final CompositeSet<E> composite,
                    final List<Set<E>> collections, final E obj) {
                throw new UnsupportedOperationException();
            }

            public boolean addAll(final CompositeSet<E> composite,
                    final List<Set<E>> collections, final Collection<? extends E> coll) {
                throw new UnsupportedOperationException();
            }
        });

        final HashSet<E> three = new HashSet<E>();
        three.add((E) "1");
        try {
            set.addComposited(three);
            fail("IllegalArgumentException should have been thrown");
        }
        catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testAddComposited() {
        final Set<E> one = buildOne();
        final Set<E> two = buildTwo();
        final CompositeSet<E> set = new CompositeSet<E>();
        set.addComposited(one, two);
        final CompositeSet<E> set2 = new CompositeSet<E>(buildOne());
        set2.addComposited(buildTwo());
        assertTrue(set.equals(set2));
        final HashSet<E> set3 = new HashSet<E>();
        set3.add((E) "1");
        set3.add((E) "2");
        set3.add((E) "3");
        final HashSet<E> set4 = new HashSet<E>();
        set4.add((E) "4");
        final CompositeSet<E> set5 = new CompositeSet<E>(set3);
        set5.addComposited(set4);
        assertTrue(set.equals(set5));
        try {
            set.addComposited(set3);
            fail("Expecting UnsupportedOperationException.");
        } catch (final UnsupportedOperationException ex) {
            // expected
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.3";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "/tmp/CompositeSet.emptyCollection.version3.3.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "/tmp/CompositeSet.fullCollection.version3.3.obj");
//    }

}
