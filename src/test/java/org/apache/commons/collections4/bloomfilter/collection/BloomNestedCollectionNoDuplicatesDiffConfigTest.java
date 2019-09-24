/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter.collection;

import org.apache.commons.collections4.bloomfilter.BloomFilterConfiguration;
import org.apache.commons.collections4.bloomfilter.BloomFilterConfiguration;
import org.apache.commons.collections4.bloomfilter.collection.BloomNestedCollection;
import org.apache.commons.collections4.bloomfilter.collection.BloomNestedCollection.BloomHashSetFactory;
import org.junit.Before;

/**
 * Tests BloomNestedCollection that does not accept duplicates and that uses different filter configurations
 * for the gate and the buckets.
 */
public class BloomNestedCollectionNoDuplicatesDiffConfigTest extends AbstractBloomCollectionNoDuplicatesTest {


    BloomFilterConfiguration gateConfig = new BloomFilterConfiguration( 25, 1.0/5 );
    BloomFilterConfiguration bucketConfig = new BloomFilterConfiguration( 5, 1.0/5 );

    /**
     * Constructor.
     */
    public BloomNestedCollectionNoDuplicatesDiffConfigTest() {
        super( 1L, 2L );
    }

    /**
     * setup
     */
    @Before
    public void setup() {
        super.setup( new BloomNestedCollection<String>(FUNC, 3, gateConfig, new BloomHashSetFactory<String>(FUNC, bucketConfig) ), gateConfig );
    }

}
