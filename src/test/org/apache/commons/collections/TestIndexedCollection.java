package org.apache.commons.collections;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class TestIndexedCollection extends AbstractDecoratedCollectionTest<String> {
    private IndexedCollection<Integer, String> indexed;

    @Before
    public void setUp() throws Exception {
        indexed = IndexedCollection.uniqueIndexedCollection(original, new Transformer<String, Integer>() {
            public Integer transform(String input) {
                return Integer.parseInt(input);
            }
        });
        decorated = indexed;
    }
    
    @Test
    public void addedObjectsCanBeRetrievedByKey() throws Exception {
        decorated.add("12");
        decorated.add("16");
        decorated.add("1");
        decorated.addAll(asList("2","3","4"));
        assertEquals("12", indexed.get(12));
        assertEquals("16", indexed.get(16));
        assertEquals("1", indexed.get(1));
        assertEquals("2", indexed.get(2));
        assertEquals("3", indexed.get(3));
        assertEquals("4", indexed.get(4));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void ensureDuplicateObjectsCauseException() throws Exception {
        decorated.add("1");
        decorated.add("1");
    }
    
    @Test
    public void decoratedCollectionIsIndexedOnCreation() throws Exception {
        original.add("1");
        original.add("2");
        original.add("3");
        
        indexed = IndexedCollection.uniqueIndexedCollection(original, new Transformer<String, Integer>() {
            public Integer transform(String input) {
                return Integer.parseInt(input);
            }
        });
        assertEquals("1", indexed.get(1));
        assertEquals("2", indexed.get(2));
        assertEquals("3", indexed.get(3));
    }
    
    @Test
    public void reindexUpdatesIndexWhenTheDecoratedCollectionIsModifiedSeparately() throws Exception {
        original.add("1");
        original.add("2");
        original.add("3");
        
        assertNull(indexed.get(1));
        assertNull(indexed.get(2));
        assertNull(indexed.get(3));
        indexed.reindex();
        assertEquals("1", indexed.get(1));
        assertEquals("2", indexed.get(2));
        assertEquals("3", indexed.get(3));
    }
}
