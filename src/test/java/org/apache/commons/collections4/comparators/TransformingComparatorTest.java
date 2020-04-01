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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.TransformerUtils;

/**
 * Test class for TransformingComparator.
 *
 */
public class TransformingComparatorTest extends AbstractComparatorTest<Integer> {

    //
    // Initialization and busywork
    //

    public TransformingComparatorTest(final String name) {
        super(name);
    }

    //
    // Set up and tear down
    //

    @Override
    public Comparator<Integer> makeObject() {
        final Comparator<String> decorated = new ComparableComparator<>();
        return ComparatorUtils.transformedComparator(decorated, TransformerUtils.<Integer>stringValueTransformer());
    }

    @Override
    @SuppressWarnings("boxing") // OK in test code
    public List<Integer> getComparableObjectsOrdered() {
        final List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        return list;
    }

    public void testEquals() {
        final Transformer<String, String> t1 = TransformerUtils.nopTransformer();
        final TransformingComparator<String, String> comp1 = new TransformingComparator<>(t1);
        final TransformingComparator<String, String> comp2 = new TransformingComparator<>(t1, comp1);

        // Checks the contract: equals-hashcode on comp1 and comp2
        assertTrue("Contract failed: equals-hashcode",
                comp1.equals(comp2) ? comp1.hashCode() == comp2.hashCode() : true);

        // Checks the contract: equals-hashcode on comp1 and comp2
        assertTrue("Contract failed: equals-hashcode",
                comp2.equals(comp1) ? comp2.hashCode() == comp1.hashCode() : true);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/TransformingComparator.version4.obj");
//    }

    //
    // The tests
    //

}
