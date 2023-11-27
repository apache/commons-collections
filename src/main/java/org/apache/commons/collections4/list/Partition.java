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
package org.apache.commons.collections4.list;

import java.util.AbstractList;
import java.util.List;

/**
 * Provides a partition view on a {@link List}.
 *
 * @since 4.0
 */
public final class Partition<T> extends AbstractList<List<T>> {
    private final List<T> list;
    private final int size;

    public Partition(final List<T> list, final int size) {
        this.list = list;
        this.size = size;
    }

    @Override
    public List<T> get(final int index) {
        final int listSize = size();
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
        }
        if (index >= listSize) {
            throw new IndexOutOfBoundsException("Index " + index + " must be less than size " +
                    listSize);
        }
        final int start = index * size;
        final int end = Math.min(start + size, list.size());
        return list.subList(start, end);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public int size() {
        return (int) Math.ceil((double) list.size() / (double) size);
    }
}