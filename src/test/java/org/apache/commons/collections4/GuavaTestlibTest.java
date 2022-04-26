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

import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.ReferenceMap;

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test uses Google's Guava Testlib testing libraries to validate the
 * contract of collection classes in Commons Collections. This was introduced
 * after COLLECTIONS-802, where the issue reported was found with Testlib,
 * with thanks to Ben Manes.
 *
 * @since 4.5.0
 * @see <a href="https://github.com/google/guava/tree/master/guava-testlib">https://github.com/google/guava/tree/master/guava-testlib</a>
 * @see <a href="https://issues.apache.org/jira/browse/COLLECTIONS-802">https://issues.apache.org/jira/browse/COLLECTIONS-802</a>
 */
public final class GuavaTestlibTest extends TestCase {

    public static Test suite() {
        TestSuite test = new TestSuite();
        test.addTest(suite("HashedMap", HashedMap::new));
        test.addTest(suite("LinkedMap", LinkedMap::new));
        test.addTest(suite("LRUMap", LRUMap::new));
        test.addTest(suite("ReferenceMap", ReferenceMap::new));
        return test;
    }

    public static Test suite(String name, Supplier<Map<String, String>> factory) {
        return MapTestSuiteBuilder.using(new TestStringMapGenerator() {
            @Override
            protected Map<String, String> create(Map.Entry<String, String>[] entries) {
                Map<String, String> map = factory.get();
                for (Map.Entry<String, String> entry : entries) {
                    map.put(entry.getKey(), entry.getValue());
                }
                return map;
            }
        })
                .named(name)
                .withFeatures(
                        CollectionSize.ANY, MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES, CollectionFeature.SUPPORTS_ITERATOR_REMOVE)
                .createTestSuite();
    }
}
