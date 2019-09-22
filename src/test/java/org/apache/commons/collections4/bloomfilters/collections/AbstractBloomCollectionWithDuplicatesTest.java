package org.apache.commons.collections4.bloomfilters.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.bloomfilters.StandardBloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;
import org.junit.Test;

/**
 * Tests for an BloomCollection that accepts duplicates.
 *
 */
public abstract class AbstractBloomCollectionWithDuplicatesTest extends AbstractBloomFilterGatedWithDuplicatesTest {

    Collection<String> collection;
    FilterConfig filterConfig;

    /**
     * Constructor.
     * 
     * @param helloCount the number of items that should be returned from a search for the 
     * proto bloom filter generated from the string "Hello" when the set { "Hello", 
     * "Hello", "World" } has been added to the collection.
     * @param candidateCount the number of items that should be returned from a getCandidates( BloomFilter )
     * call when bloom filter is build from "Hello" and the set { "Hello", 
     * "Hello", "World" } has been added to the collection.     */
    protected AbstractBloomCollectionWithDuplicatesTest( long helloCount, long candidateCount ) {
        super( helloCount, candidateCount );
    }
    
    protected void setup(Collection<String> collection, FilterConfig filterConfig) {
        if (collection instanceof BloomFilterGated)
        {
            this.collection = collection;
            this.filterConfig = filterConfig;
            super.setup((BloomFilterGated<String>) collection);
        } else {
            throw new IllegalArgumentException( "Should be BloomFilterGated instance");
        }
    }

    @Test
    public final void add_T() {
        assertTrue(collection.add("Hello"));
        assertEquals(new StandardBloomFilter(FUNC.apply("Hello"), filterConfig), gated.getGate());
        assertEquals(1, gated.getStats().getInsertCount());
        assertEquals(1, gated.getStats().getTxnCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(1, gated.getStats().getFilterCount());
        assertEquals(1, collection.size());
        assertEquals(1L, gated.count());


        assertTrue(collection.add("Hello"));
        assertTrue(collection.contains("Hello"));
        assertEquals(new StandardBloomFilter(FUNC.apply("Hello"), filterConfig), gated.getGate());
        assertEquals(2, gated.getStats().getInsertCount());
        assertEquals(2, gated.getStats().getTxnCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(2, gated.getStats().getFilterCount());
        assertEquals(2, collection.size());
        assertEquals(2L, gated.count());

        assertTrue(collection.add("World"));
        assertTrue(collection.contains("World"));
        assertNotEquals(new StandardBloomFilter(FUNC.apply("World"), filterConfig), gated.getGate());
        assertTrue(gated.inverseMatch(new StandardBloomFilter(FUNC.apply("World"), filterConfig)));
        assertEquals(3, gated.getStats().getInsertCount());
        assertEquals(3, gated.getStats().getTxnCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(3, gated.getStats().getFilterCount());
        assertEquals(3, collection.size());
        assertEquals(3L, gated.count());
    }


    @Test
    public final void addAll() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( collection.addAll( all ) );
        assertTrue(collection.contains("Hello"));
        assertTrue(collection.contains("World"));
        ProtoBloomFilter proto = FUNC.apply("Hello");
        assertTrue( gated.inverseMatch(new StandardBloomFilter(proto, filterConfig)));
        proto = FUNC.apply("World");
        assertTrue( gated.inverseMatch(new StandardBloomFilter(proto, filterConfig)));
        assertEquals(3, gated.getStats().getInsertCount());
        assertEquals(3, gated.getStats().getTxnCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(3, gated.getStats().getFilterCount());
        assertEquals(3, collection.size());
        assertEquals(3L, gated.count());
    }

    @Test
    public final void contains_Obj() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue(collection.addAll(all));
        assertTrue( collection.contains("Hello"));
        assertTrue( collection.contains("World"));
        assertFalse( collection.contains("Goodbye"));
    }


    @Test
    public final void containsAll() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( collection.addAll( all ) );

        assertTrue( collection.containsAll( all ));

        all = Arrays.asList("Hello" );
        assertTrue( collection.containsAll( all ));

        all = Arrays.asList("Goodbye", "Cruel");
        assertFalse( collection.containsAll( all ));

        all = Arrays.asList("Goodbye", "Cruel", "World");
        assertFalse( collection.containsAll( all ));


    }

    @Test
    public final void iterator() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( collection.addAll( all ) );
        Iterator<String> iter= collection.iterator();
        int count = 0;
        while (iter.hasNext())
        {
            iter.next();
            count++;
        }
        assertEquals( 3, count );
    }


    @Test
    public final void remove_Object() {
        List<String> all = Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" );
        assertTrue( collection.addAll( all ) );
        assertEquals( 5, collection.size() );

        assertFalse( collection.remove( "bird" ) );
        assertEquals( 5, collection.size() );
        assertEquals(5, gated.getStats().getInsertCount());
        assertEquals(5, gated.getStats().getTxnCount());
        assertEquals(0, gated.getStats().getDeleteCount());
        assertEquals(5, gated.getStats().getFilterCount());

        assertTrue( collection.remove( "Hello" ) );
        assertEquals( 4, collection.size() );
        assertEquals(5, gated.getStats().getInsertCount());
        assertEquals(6, gated.getStats().getTxnCount());
        assertEquals(1, gated.getStats().getDeleteCount());
        assertEquals(4, gated.getStats().getFilterCount());

    }


    @Test
    public final void removeAll() {
        List<String> all = Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" );
        assertTrue( collection.addAll( all ) );
        assertEquals( 5, collection.size() );
        assertTrue( collection.removeAll( Arrays.asList( "Hello", "World" )));
        assertEquals( 3, collection.size() );

        assertEquals(5, gated.getStats().getInsertCount());
        assertEquals(7, gated.getStats().getTxnCount());
        assertEquals(2, gated.getStats().getDeleteCount());
        assertEquals(3, gated.getStats().getFilterCount());

    }



    @Test
    public final void size() {
        assertEquals( 0, collection.size() );
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( collection.addAll( all ) );

        assertEquals( 3, collection.size());
    }

    @Test
    public final void toArray() {
        Object[] result = collection.toArray();
        assertEquals( 0, result.length );

        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( collection.addAll( all ) );

        result = collection.toArray();
        assertEquals( 3, result.length );

    }

    @Test
    public final void toArray_arry() {
        String[] result = collection.toArray( new String[3]);
        assertEquals( 3, result.length );
        assertNull( result[0] );
        assertNull( result[1] );
        assertNull( result[2] );

        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( collection.addAll( all ) );

        result = collection.toArray(new String[3]);
        assertEquals( 3, result.length );
        assertEquals( "Hello", result[0] );
        assertEquals( "Hello", result[1] );
        assertEquals( "World", result[2] );
    }

}
