package org.apache.commons.collections4.bloomfilters.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.bloomfilters.BloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for collections that do not accept duplicates
 *
 */
public class BloomCollectionTestNoDups extends AbstractBloomCollectionNoDuplicatesTest {

    Function<String, ProtoBloomFilter> FUNC = new Function<String, ProtoBloomFilter>() {
        @Override
        public ProtoBloomFilter apply(String s) {
            return ProtoBloomFilter.builder().with(s).build();
        }
    };

    Collection<String> collection;
    FilterConfig filterConfig = new FilterConfig(5, 5);
    BloomCollection<String> bloomC;

    @Before
    public void setup() {
        collection = new HashSet<String>();
        bloomC = new BloomCollection<String>(collection, filterConfig, FUNC);
    }

    @Test
    public void add_T() {
        assertTrue(bloomC.add("Hello"));
        assertTrue(collection.contains("Hello"));
        assertEquals(new BloomFilter(FUNC.apply("Hello"), filterConfig), bloomC.getGate());
        assertEquals(1, bloomC.getStats().getInsertCount());
        assertEquals(1, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(1, bloomC.getStats().getFilterCount());
        assertEquals(1, bloomC.size());
        assertEquals(1L, bloomC.count());

        
        assertFalse(bloomC.add("Hello"));
        assertTrue(collection.contains("Hello"));
        assertEquals(new BloomFilter(FUNC.apply("Hello"), filterConfig), bloomC.getGate());
        assertEquals(1, bloomC.getStats().getInsertCount());
        assertEquals(1, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(1, bloomC.getStats().getFilterCount());
        assertEquals(1, bloomC.size());
        assertEquals(1L, bloomC.count());

        assertTrue(bloomC.add("World"));
        assertTrue(collection.contains("World"));
        assertNotEquals(new BloomFilter(FUNC.apply("World"), filterConfig), bloomC.getGate());
        assertTrue(bloomC.inverseMatch(new BloomFilter(FUNC.apply("World"), filterConfig)));
        assertEquals(2, bloomC.getStats().getInsertCount());
        assertEquals(2, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(2, bloomC.getStats().getFilterCount());
        assertEquals(2, bloomC.size());
        assertEquals(2L, bloomC.count());
    }
    
    @Test
    public void add_Proto_T() {
        
        ProtoBloomFilter proto = FUNC.apply("hello");
        assertTrue(bloomC.add(proto, "Hello"));
        assertTrue(collection.contains("Hello"));
        assertEquals(new BloomFilter(proto, filterConfig), bloomC.getGate());
        assertEquals(1, bloomC.getStats().getInsertCount());
        assertEquals(1, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(1, bloomC.getStats().getFilterCount());
        assertEquals(1, bloomC.size());
        assertEquals(1L, bloomC.count());

        assertTrue(bloomC.add(proto, "hola"));
        assertTrue(collection.contains("hola"));
        assertEquals(new BloomFilter(proto, filterConfig), bloomC.getGate());
        assertEquals(2, bloomC.getStats().getInsertCount());
        assertEquals(2, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(2, bloomC.getStats().getFilterCount());
        assertEquals(2, bloomC.size());
        assertEquals(2L, bloomC.count());

        proto = FUNC.apply("wello");
        assertTrue(bloomC.add(proto, "World"));
        assertTrue(collection.contains("World"));
        assertNotEquals(new BloomFilter(proto, filterConfig), bloomC.getGate());
        assertTrue(bloomC.inverseMatch(new BloomFilter(proto, filterConfig)));
        assertEquals(3, bloomC.getStats().getInsertCount());
        assertEquals(3, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(3, bloomC.getStats().getFilterCount());
        assertEquals(3, bloomC.size());
        assertEquals(3L, bloomC.count());
    }

    @Test
    public void addAll() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        assertTrue(collection.contains("Hello"));
        assertTrue(collection.contains("World"));
        ProtoBloomFilter proto = FUNC.apply("Hello");
        assertTrue( bloomC.inverseMatch(new BloomFilter(proto, filterConfig)));
        proto = FUNC.apply("World");
        assertTrue( bloomC.inverseMatch(new BloomFilter(proto, filterConfig)));
        assertEquals(2, bloomC.getStats().getInsertCount());
        assertEquals(2, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(2, bloomC.getStats().getFilterCount());
        assertEquals(2, bloomC.size());
        assertEquals(2L, bloomC.count());
    }

    @Test
    public void clear() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        bloomC.clear();
        assertTrue( bloomC.isEmpty());
        assertTrue( collection.isEmpty());
        assertEquals( BloomFilter.EMPTY, bloomC.getGate() );
        assertEquals(0, bloomC.getStats().getInsertCount());
        assertEquals(0, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(0, bloomC.getStats().getFilterCount());
        assertEquals(0, bloomC.size());
        assertEquals(0L, bloomC.count());
    }
    
    @Test
    public void contains_Obj() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        assertTrue( bloomC.contains("Hello"));
        assertTrue( bloomC.contains("World"));
        assertFalse( bloomC.contains("Goodbye"));
    }

    @Test
    public void contains_Proto_Obj() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );

        ProtoBloomFilter proto = FUNC.apply("Hello");
        assertTrue( bloomC.contains( proto, "Hello") );
        assertFalse( bloomC.contains( proto, "Goodbye") );
        
        proto = FUNC.apply("hello");
        assertFalse( bloomC.contains( proto, "Hello") );
        assertFalse( bloomC.contains( proto, "Goodbye") );
       
    }
    
    @Test
    public void containsAll() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );

        assertTrue( bloomC.containsAll( all ));
        
        all = Arrays.asList("Hello" );
        assertTrue( bloomC.containsAll( all ));
        
        all = Arrays.asList("Goodbye", "Cruel");
        assertFalse( bloomC.containsAll( all ));
        
        all = Arrays.asList("Goodbye", "Cruel", "World");
        assertFalse( bloomC.containsAll( all ));
        
       
    }
    
    @Test
    public void count() {
        assertEquals( 0, bloomC.count() );

        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );

        assertEquals( 2, bloomC.count());
       
    }
    
    @Test
    public void distance_Filter() {
        assertEquals( 0, bloomC.distance( BloomFilter.EMPTY ) );
        ProtoBloomFilter proto = FUNC.apply("Hello");
        BloomFilter filter = new BloomFilter( proto, filterConfig );

        assertEquals( filter.getHammingWeight(), bloomC.distance( filter ));
        
        bloomC.add( "Hello");

        assertEquals( 0, bloomC.distance( filter ));
        
    }

    @Test
    public void distance_Proto() {
        assertEquals( 0, bloomC.distance( BloomFilter.EMPTY ) );
        ProtoBloomFilter proto = FUNC.apply("Hello");
        BloomFilter filter = new BloomFilter( proto, filterConfig );

        assertEquals( filter.getHammingWeight(), bloomC.distance( proto ));
        
        bloomC.add( "Hello");

        assertEquals( 0, bloomC.distance( proto ));
    }
    
    @Test
    public void getCandidates_Filter() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );

        ProtoBloomFilter proto = FUNC.apply("Hello");
        BloomFilter filter = new BloomFilter( proto, filterConfig );
        
        assertEquals( 2, bloomC.getCandidates( filter ).count() );

        proto = FUNC.apply("Goodbye");
        filter = new BloomFilter( proto, filterConfig );
        
        assertEquals( 0, bloomC.getCandidates( filter ).count() );
    }

    @Test
    public void getCandidates_Proto() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );

        ProtoBloomFilter proto = FUNC.apply("Hello");
        
        assertEquals( 2, bloomC.getCandidates( proto ).count() );

        proto = FUNC.apply("Goodbye");
        
        assertEquals( 0, bloomC.getCandidates( proto ).count() );
    }
    
    @Test
    public void getData() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        
        assertEquals( 2, bloomC.getData().count() );
    }

    @Test
    public void getGate() {
        assertEquals(  BloomFilter.EMPTY, bloomC.getGate() );
        
        
        ProtoBloomFilter proto = FUNC.apply("Hello");
        BloomFilter filter = new BloomFilter( proto, filterConfig );
        
        bloomC.add( "Hello");
        assertEquals( filter, bloomC.getGate() );
        
        proto = ProtoBloomFilter.builder().with( proto ).with( "World" ).build();
        filter = new BloomFilter( proto, filterConfig );
        bloomC.add( "World" );
        assertEquals( filter, bloomC.getGate() );
    }
    
    
    @Test
    public void getGateConfig() {
        assertEquals(  filterConfig, bloomC.getGateConfig() );
    }
    
    @Test
    public void getStats() {
        assertEquals(0, bloomC.getStats().getInsertCount());
        assertEquals(0, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(0, bloomC.getStats().getFilterCount());
        
        bloomC.add( "World");
        assertEquals(1, bloomC.getStats().getInsertCount());
        assertEquals(1, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(1, bloomC.getStats().getFilterCount());
        
        bloomC.remove("World");
        assertEquals(1, bloomC.getStats().getInsertCount());
        assertEquals(2, bloomC.getStats().getTxnCount());
        assertEquals(1, bloomC.getStats().getDeleteCount());
        assertEquals(0, bloomC.getStats().getFilterCount());
    }
    
    @Test
    public void inverseMatch_Filter() {
        assertTrue( bloomC.inverseMatch(BloomFilter.EMPTY));
        
        
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        
        BloomFilter filter = new BloomFilter( 
                ProtoBloomFilter.builder().with( "Hello").build("World"),
                filterConfig );
        assertTrue( bloomC.inverseMatch( filter ));
        
        filter = new BloomFilter( 
                ProtoBloomFilter.builder().build("World"),
                filterConfig );
        assertTrue( bloomC.inverseMatch( filter ));
        
        filter = new BloomFilter( 
                ProtoBloomFilter.builder().with( "Hello").with("World").build("Dog"),
                filterConfig );
        assertFalse( bloomC.inverseMatch( filter ));

    }
    
    @Test
    public void inverseMatch_Proto() {
        assertTrue( bloomC.inverseMatch( ProtoBloomFilter.EMPTY ));
        
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        
        ProtoBloomFilter proto = ProtoBloomFilter.builder().with( "Hello").build("World");               
        assertTrue( bloomC.inverseMatch( proto ));
        
        proto = ProtoBloomFilter.builder().build("World");
        assertTrue( bloomC.inverseMatch( proto ));
        
        proto = ProtoBloomFilter.builder().with( "Hello").with("World").build("Dog");
        assertFalse( bloomC.inverseMatch( proto ));

    }
    
    @Test
    public void isEmpty() {
        assertTrue( bloomC.isEmpty());
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        assertFalse( bloomC.isEmpty() );
    }
    
    @Test
    public void isFull() {
        
        assertFalse( bloomC.isFull() );
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        assertFalse( bloomC.isFull() );
        
        bloomC.addAll( Arrays.asList("Dog", "Cat" ) );
        assertFalse( bloomC.isFull() );
        
        bloomC.add("Bird");
        assertTrue( bloomC.isFull() );
    }
    
    @Test
    public void iterator() {
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        Iterator<String> iter= bloomC.iterator();
        int count = 0;
        while (iter.hasNext())
        {
            iter.next();
            count++;
        }
        assertEquals( 2, count );
    }
    
    @Test
    public void matches_Filter() {
        assertTrue( bloomC.matches(BloomFilter.EMPTY));
        
        
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        
        BloomFilter filter = new BloomFilter( 
                ProtoBloomFilter.builder().with( "Hello").build("World"),
                filterConfig );
        assertTrue( bloomC.matches( filter ));
        
        filter = new BloomFilter( 
                ProtoBloomFilter.builder().build("World"),
                filterConfig );
        assertFalse( bloomC.matches( filter ));
        
        filter = new BloomFilter( 
                ProtoBloomFilter.builder().with( "Hello").with("World").build("Dog"),
                filterConfig );
        assertTrue( bloomC.matches( filter ));
    }
    
    @Test
    public void matches_Proto() {
        assertTrue( bloomC.matches( ProtoBloomFilter.EMPTY ));
        
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        
        ProtoBloomFilter proto = ProtoBloomFilter.builder().with( "Hello").build("World");               
        assertTrue( bloomC.matches( proto ));
        
        proto = ProtoBloomFilter.builder().build("World");
        assertFalse( bloomC.matches( proto ));
        
        proto = ProtoBloomFilter.builder().with( "Hello").with("World").build("Dog");
        assertTrue( bloomC.matches( proto ));
    }
    
    @Test
    public void remove_Object() {
        List<String> all = Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" );
        assertTrue( bloomC.addAll( all ) );
        assertEquals( 4, bloomC.size() );

        assertFalse( bloomC.remove( "bird" ) );
        assertEquals( 4, bloomC.size() );
        assertEquals(4, bloomC.getStats().getInsertCount());
        assertEquals(4, bloomC.getStats().getTxnCount());
        assertEquals(0, bloomC.getStats().getDeleteCount());
        assertEquals(4, bloomC.getStats().getFilterCount());

        assertTrue( bloomC.remove( "Hello" ) );
        assertEquals( 3, bloomC.size() );        
        assertEquals(4, bloomC.getStats().getInsertCount());
        assertEquals(5, bloomC.getStats().getTxnCount());
        assertEquals(1, bloomC.getStats().getDeleteCount());
        assertEquals(3, bloomC.getStats().getFilterCount());

    }
    
    @Test
    public void remove_Proto_T() {
        List<String> all = Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" );
        assertTrue( bloomC.addAll( all ) );
        assertEquals( 4, bloomC.size() );
        
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build( "hello");

        assertFalse( bloomC.remove(  proto, "Hello" ) );
        assertEquals( 4, bloomC.size() );
        
        proto = ProtoBloomFilter.builder().build( "Hello");
        assertFalse( bloomC.remove( proto, "bird" ));
        assertEquals( 4, bloomC.size() );
        
        // will delete if the proto matches and the object is in the collection.
        assertTrue( bloomC.remove( proto, "Dog"));
        assertEquals( 3, bloomC.size() );
        assertEquals(4, bloomC.getStats().getInsertCount());
        assertEquals(5, bloomC.getStats().getTxnCount());
        assertEquals(1, bloomC.getStats().getDeleteCount());
        assertEquals(3, bloomC.getStats().getFilterCount());    }
    
    @Test
    public void removeAll() {
        List<String> all = Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" );
        assertTrue( bloomC.addAll( all ) );
        assertEquals( 4, bloomC.size() );
        bloomC.removeAll( Arrays.asList( "Hello", "World" ));
        assertEquals( 2, bloomC.size() );
        
        assertEquals(4, bloomC.getStats().getInsertCount());
        assertEquals(6, bloomC.getStats().getTxnCount());
        assertEquals(2, bloomC.getStats().getDeleteCount());
        assertEquals(2, bloomC.getStats().getFilterCount());
        
    }
    
    @Test
    public void retainAll() {
        List<String> all = Arrays.asList("Hello", "Hello", "World", "Cat", "Dog" );
        assertTrue( bloomC.addAll( all ) );
        assertEquals( 4, bloomC.size() );
        bloomC.retainAll( Arrays.asList( "Hello", "World" ));
        assertEquals( 2, bloomC.size() );
        
        assertEquals(4, bloomC.getStats().getInsertCount());
        assertEquals(6, bloomC.getStats().getTxnCount());
        assertEquals(2, bloomC.getStats().getDeleteCount());
        assertEquals(2, bloomC.getStats().getFilterCount());
    }
    
    @Test
    public void size() {
        assertEquals( 0, bloomC.size() );
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );

        assertEquals( 2, bloomC.size());
    }
    
    @Test
    public void toArray() {
        Object[] result = bloomC.toArray();
        assertEquals( 0, result.length );
        
        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        
        result = bloomC.toArray();
        assertEquals( 2, result.length );
        
    }
    
    @Test
    public void toArray_arry() {
        String[] result = bloomC.toArray( new String[3]);
        assertEquals( 3, result.length );
        assertNull( result[0] );
        assertNull( result[1] );
        assertNull( result[2] );

        List<String> all = Arrays.asList("Hello", "Hello", "World" );
        assertTrue( bloomC.addAll( all ) );
        
        result = bloomC.toArray(new String[3]);
        assertEquals( 3, result.length );
        assertEquals( "Hello", result[0] );
        assertEquals( "World", result[1] );
        assertNull( result[2] );
    }

}
