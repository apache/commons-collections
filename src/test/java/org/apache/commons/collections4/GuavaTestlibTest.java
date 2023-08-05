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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.ReferenceMap;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.ListFeature;
import com.google.common.collect.testing.features.MapFeature;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test uses Google's Guava Testlib testing libraries to validate the
 * contract of collection classes in Commons Collections. This was introduced
 * after COLLECTIONS-802, where the issue reported was found with Testlib.
 *
 * @since 4.5.0
 * @see <a href="https://github.com/google/guava/tree/master/guava-testlib">https://github.com/google/guava/tree/master/guava-testlib</a>
 * @see <a href="https://issues.apache.org/jira/browse/COLLECTIONS-802">https://issues.apache.org/jira/browse/COLLECTIONS-802</a>
 */
public final class GuavaTestlibTest extends TestCase {

    public static Test suite() {
        final TestSuite test = new TestSuite();
        // Map
        test.addTest(suiteMap("HashedMap", HashedMap::new));
        test.addTest(suiteMap("LinkedMap", LinkedMap::new));
        test.addTest(suiteMap("LRUMap", LRUMap::new));
        test.addTest(suiteMap("ReferenceMap", ReferenceMap::new));
        // List
        test.addTest(suiteList("TreeList", TreeList::new));
        // TODO: In COLLECTIONS-811 we enabled the list tests for TreeList, but these other two types did not
        //       pass the tests. Someone needs to confirm if it is a bug in the code, or we need to change the
        //       test features.
        // test.addTest(suiteList("GrowthList", GrowthList::new, CollectionFeature.SERIALIZABLE));
        // test.addTest(suiteList("CursorableLinkedList", CursorableLinkedList::new, CollectionFeature.SERIALIZABLE));
        return test;
    }

    /**
     * Programmatically create a JUnit (3, 4) Test Suite for Guava testlib tests with Maps.
     * @param name name of the test
     * @param factory factory to create new Maps
     * @return a JUnit 3, 4 Test Suite
     */
    private static Test suiteMap(final String name, final Supplier<Map<String, String>> factory) {
        return MapTestSuiteBuilder.using(new TestStringMapGenerator() {
            @Override
            protected Map<String, String> create(final Map.Entry<String, String>[] entries) {
                final Map<String, String> map = factory.get();
                for (final Map.Entry<String, String> entry : entries) {
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

    /**
     * Programmatically create a JUnit (3, 4) Test Suite for Guava testlib tests with Lists.
     * @param name name of the test
     * @param factory factory to create new Lists
     * @param features test features used in the tests
     * @return a JUnit 3, 4 Test Suite
     */
    private static Test suiteList(final String name, final Supplier<List<String>> factory, final Feature<?>... features) {
        final ListTestSuiteBuilder<String> suite = ListTestSuiteBuilder.using(new TestStringListGenerator() {
            @Override
            protected List<String> create(final String[] elements) {
                final List<String> list = factory.get();
                Collections.addAll(list, elements);
                return list;
            }
        })
                .named(name)
                .withFeatures(
                        CollectionSize.ANY,
                        ListFeature.GENERAL_PURPOSE,
                        ListFeature.REMOVE_OPERATIONS,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.DESCENDING_VIEW,
                        CollectionFeature.SUBSET_VIEW);
        suite.withFeatures(features);
        return suite.createTestSuite();
    }
}
