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

import org.apache.commons.collections4.iterators.ObjectArrayListIterator;

/**
 * Tests the ObjectArrayListIterator class.
 *
 * @version $Id$
 */
public class ObjectArrayListIterator2Test<E> extends AbstractListIteratorTest<E> {

    protected String[] testArray = { "One", "Two", "Three" };

    public ObjectArrayListIterator2Test(final String testName) {
        super(testName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayListIterator<E> makeEmptyIterator() {
        return new ObjectArrayListIterator<E>((E[]) new Object[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayListIterator<E> makeObject() {
        return new ObjectArrayListIterator<E>((E[]) testArray);
    }

    public ObjectArrayListIterator<E> makeArrayListIterator(final E[] array) {
        return new ObjectArrayListIterator<E>(array);
    }

    @Override
    public boolean supportsAdd() {
        return false;
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

}
