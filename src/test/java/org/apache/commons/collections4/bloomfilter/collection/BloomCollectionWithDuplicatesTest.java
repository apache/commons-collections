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

import java.util.ArrayList;

import org.apache.commons.collections4.bloomfilter.FilterConfiguration;
import org.apache.commons.collections4.bloomfilter.FilterConfiguration;
import org.apache.commons.collections4.bloomfilter.collection.BloomCollection;
import org.junit.Before;

public class BloomCollectionWithDuplicatesTest extends AbstractBloomCollectionWithDuplicatesTest {


    public BloomCollectionWithDuplicatesTest()
    {
        super( 3L, 3L );
    }
    
    @Before
    public void setup() {
        FilterConfiguration filterConfig = new FilterConfiguration(5,1.0/5);
        super.setup( new BloomCollection<String>( new ArrayList<String>(), filterConfig, FUNC ), filterConfig );
    }



}
