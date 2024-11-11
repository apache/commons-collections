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

package org.apache.commons.collections4.map;

import java.util.EnumSet;

import org.apache.commons.collections4.map.ConcurrentReferenceHashMap.Option;

/**
 * Tests {@link ConcurrentReferenceHashMap}.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public abstract class AbstractConcurrentReferenceHashMapTest<K, V> extends AbstractMapTest<ConcurrentReferenceHashMap<K, V>, K, V> {

    protected static final EnumSet<Option> IDENTITY_COMPARISONS = EnumSet.of(Option.IDENTITY_COMPARISONS);

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValueGet() {
        return false;
    }

    @Override
    public boolean isAllowNullValuePut() {
        return false;
    }

    @Override
    public boolean isTestSerialization() {
        return false;
    }

}
