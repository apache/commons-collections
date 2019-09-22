package org.apache.commons.collections4.bloomfilters.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
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
        assertEquals( BloomFilter.EMPTY, config.getGate() );
        assertTrue( new CollectionStats().sameValues(config.getStats() ));

        config.merge( ProtoBloomFilter.builder().build( "Hello"));
        config.getStats().delete();
        config.getStats().insert();

        assertNotEquals( BloomFilter.EMPTY, config.getGate() );
        assertFalse( new CollectionStats().sameValues(config.getStats() ));

        config.clear();
        assertEquals( BloomFilter.EMPTY, config.getGate() );
        assertTrue( new CollectionStats().sameValues(config.getStats() ));

    }

    @Test
    public void getConfig() {
        assertEquals( filterConfig, config.getConfig() );
    }

    @Test
    public void getGate() {
        assertEquals( BloomFilter.EMPTY, config.getGate() );
    }

    @Test
    public void getStats() {
        assertTrue( new CollectionStats().sameValues(config.getStats() ));
    }

    @Test
    public void merge_Filter() {
        assertEquals( BloomFilter.EMPTY, config.getGate() );
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build( "Hello");
        BloomFilter filter = new BloomFilter( proto, filterConfig );
        config.merge( filter );
        assertEquals( filter, config.getGate() );
    }

    @Test
    public void merge_Proto() {
        assertEquals( BloomFilter.EMPTY, config.getGate() );
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build( "Hello");
        BloomFilter filter = new BloomFilter( proto, filterConfig );
        config.merge( proto );
        assertEquals( filter, config.getGate() );
    }
}
