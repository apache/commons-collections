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
package org.apache.commons.collections4.iterators;

import org.junit.Test;

import java.util.Enumeration;
import java.util.LinkedList;

import static org.easymock.EasyMock.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;


/**
 * Unit tests for class {@link EnumerationIterator}.
 *
 * @date 25.06.2017
 * @see EnumerationIterator
 **/
public class EnumerationIteratorTest {


    @Test
    public void testRemoveThrowsIllegalStateException() {

        LinkedList<Object> linkedList = new LinkedList<Object>();
        Enumeration<Integer> enumeration = (Enumeration<Integer>) mock(Enumeration.class);
        EnumerationIterator<Integer> enumerationIterator = new EnumerationIterator<Integer>(enumeration, linkedList);

        try {
            enumerationIterator.remove();
            fail("Expecting exception: IllegalStateException");
        } catch(IllegalStateException e) {
            assertEquals("next() must have been called for remove() to function",e.getMessage());
            assertEquals(EnumerationIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testRemoveThrowsUnsupportedOperationException() {

        Enumeration<String> enumeration = (Enumeration<String>) mock(Enumeration.class);
        EnumerationIterator<String> enumerationIterator = new EnumerationIterator<String>(enumeration);

        try {
            enumerationIterator.remove();
            fail("Expecting exception: UnsupportedOperationException");
        } catch(UnsupportedOperationException e) {
            assertEquals("No Collection associated with this Iterator",e.getMessage());
            assertEquals(EnumerationIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test(expected = NullPointerException.class)
    public void testCreatesEnumerationIteratorTakingNoArgumentsAndRaisesNullPointerException() {

        EnumerationIterator<String> enumerationIterator = new EnumerationIterator<String>();

        assertFalse(enumerationIterator.hasNext());

    }


}