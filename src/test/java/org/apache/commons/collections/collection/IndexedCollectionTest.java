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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.IndexedCollection;
import org.junit.Test;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the 
 * {@link IndexedCollection} implementation.
 *
 * @since 4.0
 * @version $Id$
 */
@SuppressWarnings("boxing")
public class IndexedCollectionTest extends AbstractCollectionTest<String> {

    public IndexedCollectionTest(String name) {
        super(name);
    }

   //------------------------------------------------------------------------

    protected Collection<String> decorateCollection(Collection<String> collection) {
        return IndexedCollection.uniqueIndexedCollection(collection, new IntegerTransformer());
    }

    private static final class IntegerTransformer implements Transformer<String, Integer>, Serializable {
        private static final long serialVersionUID = 809439581555072949L;

        public Integer transform(String input) {
            return Integer.valueOf(input);
        }
    }
    
    @Override
    public Collection<String> makeObject() {
        return decorateCollection(new ArrayList<String>());
    }

    @Override
    public Collection<String> makeConfirmedCollection() {
        return new ArrayList<String>();
    }

    @Override
    public String[] getFullElements() {
        return (String[]) new String[] { "1", "3", "5", "7", "2", "4", "6" };
    }

    @Override
    public String[] getOtherElements() {
        return new String[] {"9", "88", "678", "87", "98", "78", "99"};
    }

    @Override
    public Collection<String> makeFullCollection() {
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(getFullElements()));
        return decorateCollection(list);
    }

    @Override
    public Collection<String> makeConfirmedFullCollection() {
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    @Override
    protected boolean skipSerializedCanonicalTests() {
        // FIXME: support canonical tests
        return true;
    }

    //------------------------------------------------------------------------

    public void testCollectionAddAll() {
        // FIXME: does not work as we do not support multi-keys yet
    }

    @Test
    public void addedObjectsCanBeRetrievedByKey() throws Exception {
        Collection<String> coll = getCollection();
        coll.add("12");
        coll.add("16");
        coll.add("1");
        coll.addAll(asList("2","3","4"));
        
        @SuppressWarnings("unchecked")
        IndexedCollection<Integer, String> indexed = (IndexedCollection<Integer, String>) coll;
        assertEquals("12", indexed.get(12));
        assertEquals("16", indexed.get(16));
        assertEquals("1", indexed.get(1));
        assertEquals("2", indexed.get(2));
        assertEquals("3", indexed.get(3));
        assertEquals("4", indexed.get(4));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void ensureDuplicateObjectsCauseException() throws Exception {
        getCollection().add("1");
        getCollection().add("1");
    }
    
//    @Test
//    public void decoratedCollectionIsIndexedOnCreation() throws Exception {
//        original.add("1");
//        original.add("2");
//        original.add("3");
//        
//        indexed = IndexedCollection.uniqueIndexedCollection(original, new Transformer<String, Integer>() {
//            public Integer transform(String input) {
//                return Integer.parseInt(input);
//            }
//        });
//        assertEquals("1", indexed.get(1));
//        assertEquals("2", indexed.get(2));
//        assertEquals("3", indexed.get(3));
//    }
//    
//    @Test
//    public void reindexUpdatesIndexWhenTheDecoratedCollectionIsModifiedSeparately() throws Exception {
//        original.add("1");
//        original.add("2");
//        original.add("3");
//        
//        assertNull(indexed.get(1));
//        assertNull(indexed.get(2));
//        assertNull(indexed.get(3));
//        indexed.reindex();
//        assertEquals("1", indexed.get(1));
//        assertEquals("2", indexed.get(2));
//        assertEquals("3", indexed.get(3));
//    }
}
