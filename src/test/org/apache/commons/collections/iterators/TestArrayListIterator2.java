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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test the ArrayListIterator class with primitives.
 *
 * @version $Revision$ $Date$
 * @author Neil O'Toole
 */
public class TestArrayListIterator2<E> extends TestArrayIterator2<E> {

    public TestArrayListIterator2(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestArrayListIterator2.class);
    }

    public ArrayListIterator<E> makeEmptyIterator() {
        return new ArrayListIterator<E>(new int[0]);
    }

    public ArrayListIterator<E> makeObject() {
        return new ArrayListIterator<E>(testArray);
    }

    public ArrayListIterator<E> makeArrayListIterator(Object array) {
        return new ArrayListIterator<E>(array);
    }

    public ArrayListIterator<E> makeArrayListIterator(Object array, int index) {
        return new ArrayListIterator<E>(array, index);
    }

    public ArrayListIterator<E> makeArrayListIterator(Object array, int start, int end) {
        return new ArrayListIterator<E>(array, start, end);
    }

}
