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

package org.apache.commons.collections4;

import java.util.*;

public class SetOperationCardinalityHelper<O> extends CardinalityHelper<O> implements Iterable<O> {
    /**
     * Contains the unique elements of the two collections.
     */
    private final Set<O> elements;

    /**
     * Output collection.
     */
    private final List<O> newList;

    /**
     * Create a new set operation helper from the two collections.
     *
     * @param a the first collection
     * @param b the second collection
     */
    SetOperationCardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        super(a, b);
        elements = new HashSet<>();
        CollectionUtils.addAll(elements, a);
        CollectionUtils.addAll(elements, b);
        // the resulting list must contain at least each unique element, but may grow
        newList = new ArrayList<>(elements.size());
    }

    @Override
    public Iterator<O> iterator() {
        return elements.iterator();
    }

    /**
     * Returns the resulting collection.
     *
     * @return the result
     */
    public Collection<O> list() {
        return newList;
    }

    /**
     * Add the object {@code count} times to the result collection.
     *
     * @param obj   the object to add
     * @param count the count
     */
    public void setCardinality(final O obj, final int count) {
        for (int i = 0; i < count; i++) {
            newList.add(obj);
        }
    }
}
