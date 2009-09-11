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
package org.apache.commons.collections.map;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Entry point for tests.
 * 
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 * 
 * @author Stephen Colebourne
 * @author Stephen Kestle
 */
@RunWith(Suite.class)
@SuiteClasses({
    TestCaseInsensitiveMap.class,
    TestCompositeMap.class,
    TestDefaultedMap.class,
    TestFlat3Map.class,
    TestHashedMap.class,
    TestIdentityMap.class,
    TestLinkedMap.class,
    TestLRUMap.class,
    TestMultiKeyMap.class,
    TestReferenceMap.class,
    TestReferenceIdentityMap.class,
    TestStaticBucketMap.class,
    TestSingletonMap.class,
    
    TestFixedSizeMap.class,
    TestFixedSizeSortedMap.class,
    TestLazyMap.class,
    TestLazySortedMap.class,
    TestListOrderedMap.class,
    TestListOrderedMap2.class,
    TestMultiValueMap.class,
    TestPredicatedMap.class,
    TestPredicatedSortedMap.class,
    TestTransformedMap.class,
    TestTransformedSortedMap.class,
    TestUnmodifiableMap.class,
    TestUnmodifiableOrderedMap.class,
    TestUnmodifiableSortedMap.class
})
public class TestAll extends TestCase {
}
