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
package org.apache.commons.collections.collection;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.apache.commons.collections.AbstractDecoratedCollectionTest;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.IndexedCollection;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("boxing")
public class IndexedCollectionTest extends AbstractDecoratedCollectionTest<String> {
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
