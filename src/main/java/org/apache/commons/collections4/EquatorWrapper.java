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

/**
 * Wraps another object and uses the provided Equator to implement
 * {@link #equals(Object)} and {@link #hashCode()}.
 * <p>
 * This class can be used to store objects into a Map.
 * </p>
 *
 * @param <O> the element type
 * @since 4.0
 */
public class EquatorWrapper<O> {
    private final Equator<? super O> equator;
    private final O object;

    EquatorWrapper(final Equator<? super O> equator, final O object) {
        this.equator = equator;
        this.object = object;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof EquatorWrapper)) {
            return false;
        }
        @SuppressWarnings("unchecked") final EquatorWrapper<O> otherObj = (EquatorWrapper<O>) obj;
        return equator.equate(object, otherObj.getObject());
    }

    public O getObject() {
        return object;
    }

    @Override
    public int hashCode() {
        return equator.hash(object);
    }
}
