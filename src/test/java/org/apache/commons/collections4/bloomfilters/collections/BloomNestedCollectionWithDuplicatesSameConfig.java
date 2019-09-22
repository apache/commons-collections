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
package org.apache.commons.collections4.bloomfilters.collections;

import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.collections.BloomNestedCollection.BloomArrayListFactory;
import org.junit.Before;

public class BloomNestedCollectionWithDuplicatesSameConfig extends AbstractBloomCollectionWithDuplicatesTest {


    FilterConfig gateConfig = new FilterConfig( 25, 5 );
    FilterConfig bucketConfig = new FilterConfig( 25, 5 );
    
    public BloomNestedCollectionWithDuplicatesSameConfig() {
        super( 2L, 2L );
    }
    @Before
    public void setup() {        
        super.setup( new BloomNestedCollection<String>(FUNC, 3, gateConfig, new BloomArrayListFactory<String>(FUNC, bucketConfig) ), gateConfig );
    }

}
