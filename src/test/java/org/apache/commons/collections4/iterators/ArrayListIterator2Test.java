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

/**
 * Test the ArrayListIterator class with primitives.
 *
 * @version $Id$
 */
public class ArrayListIterator2Test<E> extends ArrayIterator2Test<E> {

    public ArrayListIterator2Test(final String testName) {
        super(testName);
    }

    @Override
    public ArrayListIterator<E> makeEmptyIterator() {
        return new ArrayListIterator<>(new int[0]);
    }

    @Override
    public ArrayListIterator<E> makeObject() {
        return new ArrayListIterator<>(testArray);
    }

    public ArrayListIterator<E> makeArrayListIterator(final Object array) {
        return new ArrayListIterator<>(array);
    }

    public ArrayListIterator<E> makeArrayListIterator(final Object array, final int index) {
        return new ArrayListIterator<>(array, index);
    }

    public ArrayListIterator<E> makeArrayListIterator(final Object array, final int start, final int end) {
        return new ArrayListIterator<>(array, start, end);
    }

}
