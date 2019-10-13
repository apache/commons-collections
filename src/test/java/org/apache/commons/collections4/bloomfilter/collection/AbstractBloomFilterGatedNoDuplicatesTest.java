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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter;
import org.apache.commons.collections4.bloomfilter.StandardBloomFilter;
import org.apache.commons.collections4.bloomfilter.collection.BloomFilterGated;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.Test;

/**
 * Tests for a BloomFilterGated implementations that does not accept duplicates.
 *
 */
public abstract class AbstractBloomFilterGatedNoDuplicatesTest {

    public static Function<String, ProtoBloomFilter> FUNC = new Function<String, ProtoBloomFilter>() {
        @Override
        public ProtoBloomFilter apply(String s) {
            return ProtoBloomFilter.builder().with(s).build();
        }
    };

    protected BloomFilterGated<String> gated;

    private final long helloCount;
    private final long candidateCount;

    /**
     * Constructor.
     *
     * @param helloCount the number of items that should be returned from a search for the
     * proto bloom filter generated from the string "Hello" when the set { "Hello",
     * "Hello", "World" } has been added to the collection.
     * @param candidateCount the number of items that should be returned from a getCandidates( BloomFilter )
     * call when bloom filter is build from "Hello" and the set { "Hello",
     * "Hello", "World" } has been added to the collection.     */
    protected AbstractBloomFilterGatedNoDuplicatesTest(long helloCount, long candidateCount)
    {
        this.helloCount = helloCount;
        this.candidateCount = candidateCount;
    }

    protected void setup(BloomFilterGated<String> bloomC) {
        this.gated = bloomC;
    }

    private void addAll( List<String> objs )
    {
        for (String s : objs ) {
            gated.add( FUNC.apply(s), s );
        }
    }

    @Test
    public final void getGateConfig() {
        assertNotNull( gated.getGateConfig() );
    }

    @Test
    public final void add_Proto_T() {

        ProtoBloomFilter proto = FUNC.apply("hello");
        assertTrue(gated.add(proto, "Hello"));
        assertEquals(new StandardBloomFilter(proto, gated.getGateConfig()), gated.getGate());
        assertEquals(1, gated.getStats().getInsertCount());
        assertEquals(1, gated.getStats().getTransactionCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(1, gated.getStats().getFilterCount());
        assertEquals(1L, gated.count());

        assertTrue(gated.add(proto, "hola"));
        assertEquals(new StandardBloomFilter(proto, gated.getGateConfig()), gated.getGate());
        assertEquals(2, gated.getStats().getInsertCount());
        assertEquals(2, gated.getStats().getTransactionCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(2, gated.getStats().getFilterCount());
        assertEquals(2L, gated.count());

        proto = FUNC.apply("wello");
        assertTrue(gated.add(proto, "World"));
        assertNotEquals(new StandardBloomFilter(proto, gated.getGateConfig()), gated.getGate());
        assertTrue(gated.inverseMatch(new StandardBloomFilter(proto, gated.getGateConfig())));
        assertEquals(3, gated.getStats().getInsertCount());
        assertEquals(3, gated.getStats().getTransactionCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(3, gated.getStats().getFilterCount());
        assertEquals(3L, gated.count());
    }

    @Test
    public final void clear() {
        addAll( Arrays.asList("Hello", "Hello", "World" ) );
        gated.clear();
        assertTrue( gated.isEmpty());
        assertEquals( StandardBloomFilter.EMPTY, gated.getGate() );
        assertEquals(0, gated.getStats().getInsertCount());
        assertEquals(0, gated.getStats().getTransactionCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(0, gated.getStats().getFilterCount());
        assertEquals(0L, gated.count());
    }


    @Test
    public final void contains_Proto_Obj() {
        addAll( Arrays.asList("Hello", "Hello", "World" ) );

        ProtoBloomFilter proto = FUNC.apply("Hello");
        assertTrue( gated.contains( proto, "Hello") );
        assertFalse( gated.contains( proto, "Goodbye") );

        proto = FUNC.apply("hello");
        assertFalse( gated.contains( proto, "Hello") );
        assertFalse( gated.contains( proto, "Goodbye") );

    }
    @Test
    public final void count() {
        assertEquals( 0, gated.count() );

        addAll( Arrays.asList("Hello", "Hello", "World" ) );

        assertEquals( 2, gated.count());

    }

    @Test
    public final void distance_Filter() {
        assertEquals( 0, gated.distance( StandardBloomFilter.EMPTY ) );
        ProtoBloomFilter proto = FUNC.apply("Hello");
        StandardBloomFilter filter = new StandardBloomFilter( proto, gated.getGateConfig() );

        assertEquals( filter.getHammingWeight(), gated.distance( filter ));

        gated.add( proto, "Hello");

        assertEquals( 0, gated.distance( filter ));

    }

    @Test
    public final void distance_Proto() {
        assertEquals( 0, gated.distance( StandardBloomFilter.EMPTY ) );
        ProtoBloomFilter proto = FUNC.apply("Hello");
        BloomFilter filter = new StandardBloomFilter( proto, gated.getGateConfig() );

        assertEquals( filter.getHammingWeight(), gated.distance( proto ));

        gated.add( proto, "Hello");

        assertEquals( 0, gated.distance( proto ));
    }

    @Test
    public final void getCandidates_Filter() {
        addAll( Arrays.asList("Hello", "Hello", "World" ) );

        ProtoBloomFilter proto = FUNC.apply("Hello");
        BloomFilter filter = new StandardBloomFilter( proto, gated.getGateConfig() );

        assertEquals( candidateCount, gated.getCandidates( filter ).count() );

        proto = FUNC.apply("Goodbye");
        filter = new StandardBloomFilter( proto, gated.getGateConfig() );

        assertEquals( 0, gated.getCandidates( filter ).count() );
    }

    @Test
    public final void getCandidates_Proto() {
        addAll( Arrays.asList("Hello", "Hello", "World" ) );

        ProtoBloomFilter proto = FUNC.apply("Hello");

        assertEquals( helloCount, gated.getCandidates( proto ).count() );

        proto = FUNC.apply("Goodbye");

        assertEquals( 0, gated.getCandidates( proto ).count() );
    }

    @Test
    public final void getData() {
        addAll( Arrays.asList("Hello", "Hello", "World" ));
        assertEquals( 2, gated.getData().count() );
    }

    @Test
    public final void getGate() {
        assertEquals(  StandardBloomFilter.EMPTY, gated.getGate() );


        ProtoBloomFilter proto = FUNC.apply("Hello");
        BloomFilter filter = new StandardBloomFilter( proto, gated.getGateConfig() );

        gated.add( proto, "Hello");
        assertEquals( filter, gated.getGate() );

        proto = ProtoBloomFilter.builder().with( proto ).with( "World" ).build();
        filter = new StandardBloomFilter( proto, gated.getGateConfig() );
        gated.add( FUNC.apply("World"), "World" );
        assertEquals( filter, gated.getGate() );
    }


    @Test
    public final void getStats() {
        assertEquals(0, gated.getStats().getInsertCount());
        assertEquals(0, gated.getStats().getTransactionCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(0, gated.getStats().getFilterCount());

        ProtoBloomFilter proto = FUNC.apply("World");
        gated.add( proto,"World");
        assertEquals(1, gated.getStats().getInsertCount());
        assertEquals(1, gated.getStats().getTransactionCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(1, gated.getStats().getFilterCount());

        gated.remove(proto,"World");
        assertEquals(1, gated.getStats().getInsertCount());
        assertEquals(2, gated.getStats().getTransactionCount());
        assertEquals(1, gated.getStats().getDeleteCount());
        assertEquals(0, gated.getStats().getFilterCount());
    }

    @Test
    public final void inverseMatch_Filter() {
        assertTrue( gated.inverseMatch(StandardBloomFilter.EMPTY));


        addAll( Arrays.asList("Hello", "Hello", "World" ) );

        BloomFilter filter = new StandardBloomFilter(
                ProtoBloomFilter.builder().with( "Hello").with("World").build(),
                gated.getGateConfig() );
        assertTrue( gated.inverseMatch( filter ));

        filter = new StandardBloomFilter(
                ProtoBloomFilter.builder().with("World").build(),
                gated.getGateConfig() );
        assertTrue( gated.inverseMatch( filter ));

        filter = new StandardBloomFilter(
                ProtoBloomFilter.builder().with( "Hello").with("World").with("Dog").build(),
                gated.getGateConfig() );
        assertFalse( gated.inverseMatch( filter ));

    }

    @Test
    public final void inverseMatch_Proto() {
        assertTrue( gated.inverseMatch( ProtoBloomFilter.EMPTY ));

        addAll( Arrays.asList("Hello", "Hello", "World" ) );

        ProtoBloomFilter proto = ProtoBloomFilter.builder().with( "Hello").with("World").build();
        assertTrue( gated.inverseMatch( proto ));

        proto = ProtoBloomFilter.builder().with("World").build();
        assertTrue( gated.inverseMatch( proto ));

        proto = ProtoBloomFilter.builder().with( "Hello").with("World").with("Dog").build();
        assertFalse( gated.inverseMatch( proto ));

    }

    @Test
    public final void isEmpty() {
        assertTrue( gated.isEmpty());
        addAll( Arrays.asList("Hello", "Hello", "World" ));
        assertFalse( gated.isEmpty() );
    }

    @Test
    public final void isFull() {
        for (int i=0;i<gated.getGateConfig().getNumberOfItems();i++)
        {
            assertFalse( gated.isFull() );
            gated.add( FUNC.apply( "Word"+i), "Word"+i);
        }
        assertTrue( gated.isFull() );
    }



    @Test
    public final void matches_Filter() {
        assertTrue( gated.matches(StandardBloomFilter.EMPTY));


        addAll(Arrays.asList("Hello", "Hello", "World" ));

        StandardBloomFilter filter = new StandardBloomFilter(
                ProtoBloomFilter.builder().with( "Hello").with("World").build(),
                gated.getGateConfig() );
        assertTrue( gated.matches( filter ));

        filter = new StandardBloomFilter(
                ProtoBloomFilter.builder().with("World").build(),
                gated.getGateConfig() );
        assertFalse( gated.matches( filter ));

        filter = new StandardBloomFilter(
                ProtoBloomFilter.builder().with( "Hello").with("World").with("Dog").build(),
                gated.getGateConfig() );
        assertTrue( gated.matches( filter ));
    }

    @Test
    public final void matches_Proto() {
        assertTrue( gated.matches( ProtoBloomFilter.EMPTY ));

        addAll( Arrays.asList("Hello", "Hello", "World" ) );

        ProtoBloomFilter proto = ProtoBloomFilter.builder().with( "Hello").with("World").build();
        assertTrue( gated.matches( proto ));

        proto = ProtoBloomFilter.builder().with("World").build();
        assertFalse( gated.matches( proto ));

        proto = ProtoBloomFilter.builder().with( "Hello").with("World").with("Dog").build();
        assertTrue( gated.matches( proto ));
    }

    @Test
    public final void remove_Proto_T() {
        addAll( Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" ) );
        assertEquals( 4, gated.count() );

        // wrong filter correct value
        ProtoBloomFilter proto = FUNC.apply( "hello");
        assertFalse( gated.remove(  proto, "Hello" ) );
        assertEquals( 4, gated.count() );

        // correct filter wrong value
        proto = FUNC.apply( "Hello");
        assertFalse( gated.remove( proto, "bird" ));
        assertEquals( 4, gated.count() );

        // correct filter correct value
        assertTrue( gated.remove( proto, "Hello" ) );
        assertEquals( 3, gated.count() );

        // will delete if the proto matches and the object is in the collection.
        assertTrue( gated.remove( proto, "Dog"));
        assertEquals( 2, gated.count() );
        assertEquals(4, gated.getStats().getInsertCount());
        assertEquals(6, gated.getStats().getTransactionCount());
        assertEquals(2, gated.getStats().getDeleteCount());
        assertEquals(2, gated.getStats().getFilterCount());
    }


    @Test
    public final void retainAll_MultiValuedMap() {

        addAll( Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" ) );

        assertEquals( 4, gated.count() );

        MultiValuedMap<ProtoBloomFilter, String> map = new ArrayListValuedHashMap<ProtoBloomFilter, String>();
        Arrays.asList( "Hello", "World" ).stream().forEach(t -> map.put(FUNC.apply(t), t));

        gated.retainAll( map );
        assertEquals( 2, gated.count() );

        assertEquals(4, gated.getStats().getInsertCount());
        assertEquals(6, gated.getStats().getTransactionCount());
        assertEquals(2, gated.getStats().getDeleteCount());
        assertEquals(2, gated.getStats().getFilterCount());
    }

}
