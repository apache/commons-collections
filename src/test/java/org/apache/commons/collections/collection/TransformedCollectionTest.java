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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the {@link TransformedCollection}
 * implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class TransformedCollectionTest extends AbstractCollectionTest<Object> {
    
    private static class StringToInteger implements Transformer<Object, Object> {
        public Object transform(Object input) {
            return new Integer((String) input);
        }
    }
    
    public static final Transformer<Object, Object> NOOP_TRANSFORMER = TransformerUtils.nopTransformer();
    public static final Transformer<Object, Object> STRING_TO_INTEGER_TRANSFORMER = new StringToInteger();

    public TransformedCollectionTest(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public Collection<Object> makeConfirmedCollection() {
        return new ArrayList<Object>();
    }

    @Override
    public Collection<Object> makeConfirmedFullCollection() {
        List<Object> list = new ArrayList<Object>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }
    
    @Override
    public Collection<Object> makeObject() {
        return TransformedCollection.transformingCollection(new ArrayList<Object>(), NOOP_TRANSFORMER);
    }

    @Override
    public Collection<Object> makeFullCollection() {
        List<Object> list = new ArrayList<Object>();
        list.addAll(Arrays.asList(getFullElements()));
        return TransformedCollection.transformingCollection(list, NOOP_TRANSFORMER);
    }
    
    //-----------------------------------------------------------------------
    @Override
    public Object[] getFullElements() {
        return new Object[] {"1", "3", "5", "7", "2", "4", "6"};
    }

    @Override
    public Object[] getOtherElements() {
        return new Object[] {"9", "88", "678", "87", "98", "78", "99"};
    }

    //-----------------------------------------------------------------------
    public void testTransformedCollection() {
        Collection<Object> coll = TransformedCollection.transformingCollection(new ArrayList<Object>(), STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, coll.size());
        Object[] els = getFullElements();
        for (int i = 0; i < els.length; i++) {
            coll.add(els[i]);
            assertEquals(i + 1, coll.size());
            assertEquals(true, coll.contains(new Integer((String) els[i])));
            assertEquals(false, coll.contains(els[i]));
        }
        
        assertEquals(true, coll.remove(new Integer((String) els[0])));
    }

    public void testTransformedCollection_decorateTransform() {
        Collection originalCollection = new ArrayList();
        Object[] els = getFullElements();
        for (Object el : els) {
            originalCollection.add(el);
        }
        Collection<Object> collection = TransformedCollection.transformedCollection(originalCollection, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(els.length, collection.size());
        for (Object el : els) {
            assertEquals(true, collection.contains(new Integer((String) el)));
            assertEquals(false, collection.contains(el));
        }
        
        assertEquals(false, collection.remove(els[0]));
        assertEquals(true, collection.remove(new Integer((String) els[0])));
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/TransformedCollection.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/TransformedCollection.fullCollection.version3.1.obj");
//    }

}
