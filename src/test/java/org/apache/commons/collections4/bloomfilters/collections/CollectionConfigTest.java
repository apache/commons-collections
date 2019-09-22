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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.apache.commons.collections4.bloomfilters.StandardBloomFilter;
import org.apache.commons.collections4.bloomfilters.BloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;
import org.junit.Before;
import org.junit.Test;

public class CollectionConfigTest {

    FilterConfig filterConfig = new FilterConfig(5, 5);
    CollectionConfig config;

    @Before
    public void setup() {
        config = new CollectionConfig( filterConfig );
    }

    @Test
    public void clear() {
        assertEquals( StandardBloomFilter.EMPTY, config.getGate() );
        assertTrue( new CollectionStats().sameValues(config.getStats() ));

        config.merge( ProtoBloomFilter.builder().build( "Hello"));
        config.getStats().delete();
        config.getStats().insert();

        assertNotEquals( StandardBloomFilter.EMPTY, config.getGate() );
        assertFalse( new CollectionStats().sameValues(config.getStats() ));

        config.clear();
        assertEquals( StandardBloomFilter.EMPTY, config.getGate() );
        assertTrue( new CollectionStats().sameValues(config.getStats() ));

    }

    @Test
    public void getConfig() {
        assertEquals( filterConfig, config.getConfig() );
    }

    @Test
    public void getGate() {
        assertEquals( StandardBloomFilter.EMPTY, config.getGate() );
    }

    @Test
    public void getStats() {
        assertTrue( new CollectionStats().sameValues(config.getStats() ));
    }

    @Test
    public void merge_Filter() {
        assertEquals( StandardBloomFilter.EMPTY, config.getGate() );
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build( "Hello");
        StandardBloomFilter filter = new StandardBloomFilter( proto, filterConfig );
        config.merge( filter );
        assertEquals( filter, config.getGate() );
    }

    @Test
    public void merge_Proto() {
        assertEquals( StandardBloomFilter.EMPTY, config.getGate() );
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build( "Hello");
        BloomFilter filter = new StandardBloomFilter( proto, filterConfig );
        config.merge( proto );
        assertEquals( filter, config.getGate() );
    }
}
