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

import java.util.concurrent.ConcurrentHashMap;

/**
 * A sanity test for the test framework.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class ConcurrentHashMapSanityTest<K, V> extends AbstractMapTest<ConcurrentHashMap<K, V>, K, V> {

    public ConcurrentHashMapSanityTest() {
        super(ConcurrentHashMapSanityTest.class.getSimpleName());
    }

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

    /**
     * Don't test, just a sanity check for the test framework.
     */
    @Override
    public boolean isTestSerialization() {
        return false;
    }

    @Override
    public ConcurrentHashMap<K, V> makeObject() {
        return new ConcurrentHashMap<>();
    }

}
