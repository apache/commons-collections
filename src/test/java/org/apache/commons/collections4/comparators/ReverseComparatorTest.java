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
package org.apache.commons.collections4.comparators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.ReverseComparator;
import org.junit.Test;

/**
 * Tests for ReverseComparator.
 *
 * @version $Id$
 */
public class ReverseComparatorTest extends AbstractComparatorTest<Integer> {

    public ReverseComparatorTest(final String testName) {
        super(testName);
    }

    /**
     * For the purposes of this test, return a
     * ReverseComparator that wraps the java.util.Collections.reverseOrder()
     * Comparator.  The resulting comparator should
     * sort according to natural Order.  (Note: we wrap
     * a Comparator taken from the JDK so that we can
     * save a "canonical" form in SVN.
     *
     * @return Comparator that returns "natural" order
     */
    @Override
    public Comparator<Integer> makeObject() {
        return new ReverseComparator<Integer>(Collections.<Integer>reverseOrder());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/ReverseComparator.version4.obj");
//    }
    
    @Override
    public List<Integer> getComparableObjectsOrdered() {
        final List<Integer> list = new LinkedList<Integer>();
        list.add(new Integer(1));
        list.add(new Integer(2));
        list.add(new Integer(3));
        list.add(new Integer(4));
        list.add(new Integer(5));
        return list;
    }

    /**
     * Override this inherited test since Collections.reverseOrder
     * doesn't adhere to the "soft" Comparator contract, and we've
     * already "canonized" the comparator returned by makeComparator.
     */
    @Override
    @Test
    public void testSerializeDeserializeThenCompare() throws Exception {
        final Comparator<?> comp = new ReverseComparator<String>(new ComparableComparator<String>());

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(buffer);
        out.writeObject(comp);
        out.close();

        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        final Object dest = in.readObject();
        in.close();
        assertEquals("obj != deserialize(serialize(obj))",comp,dest);
    }

}
