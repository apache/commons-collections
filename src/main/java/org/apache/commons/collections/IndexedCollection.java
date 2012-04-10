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
package org.apache.commons.collections;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.collections.collection.AbstractCollectionDecorator;

/**
 * An IndexedCollection is a Map-like view onto a Collection. It accepts a
 * keyTransformer to define how the keys are converted from the values.
 * <p>
 * Modifications made to this decorator modify the index as well as the
 * decorated {@link Collection}. However, modifications to the underlying
 * {@link Collection} will not updated the index and it will get out of sync.
 * <p>
 * If modification to the decorated {@link Collection} is unavoidable, then a
 * call to {@link #reindex()} will update the index to the current contents of
 * the {@link Collection}.
 *
 * @param <K> the type of object in the index.
 * @param <C> the type of object in the collection.
 * @author Stephen Kestle
 */
// TODO support MultiMap/non-unique index behavior
// TODO add support for remove and clear
public class IndexedCollection<K, C> extends AbstractCollectionDecorator<C> {
    /**
     * .
     */
    private static final long serialVersionUID = -5512610452568370038L;

    /**
     * Create an {@link IndexedCollection} for a unique index.
     *
     * @param <K> the index object type.
     * @param <C> the collection type.
     * @param coll the decorated {@link Collection}.
     * @param keyTransformer the {@link Transformer} for generating index keys.
     * @return the created {@link IndexedCollection}.
     */
    public static <K, C> IndexedCollection<K, C> uniqueIndexedCollection(final Collection<C> coll, final Transformer<C, K> keyTransformer) {
        return new IndexedCollection<K, C>(coll, keyTransformer, new HashMap<K, C>());
    }

    /**
     * The {@link Transformer} for generating index keys.
     */
    private final Transformer<C, K> keyTransformer;

    /**
     * The map of indexes to collected objects.
     */
    private final HashMap<K, C> index;

    /**
     * Create a {@link IndexedCollection} for a unique index.
     *
     * @param coll the decorated {@link Collection}.
     * @param keyTransformer the {@link Transformer} for generating index keys.
     */
    public IndexedCollection(Collection<C> coll, Transformer<C, K> keyTransformer, HashMap<K, C> map) {
        super(coll);
        this.keyTransformer = keyTransformer;
        this.index = map;
        reindex();
    }

    /**
     * Clears the index and re-indexes the entire decorated {@link Collection}.
     */
    public void reindex() {
        index.clear();
        for (C c : decorated()) {
            addIndex(c);
        }
    }

    /**
     * Adds an object to the collection and index.
     */
    @Override
    // TODO: Add error handling for when super.add() fails
    public boolean add(C object) {
        addIndex(object);
        return super.add(object);
    }

    /**
     * Adds an entire collection to the collection and index.
     */
    @Override
    // TODO: Add error handling for when super.addAll() fails
    public boolean addAll(Collection<? extends C> coll) {
        for (C c : coll) {
            addIndex(c);
        }
        return super.addAll(coll);
    }

    /**
     * Provides checking for adding the index.
     *
     * @param object the object to index.
     */
    private void addIndex(C object) {
        final C existingObject = index.put(keyTransformer.transform(object), object);
        if (existingObject != null) {
            throw new IllegalArgumentException("Duplicate key in uniquely indexed collection.");
        }
    }

    /**
     * Get the element associated with the given key.
     * @param key to look up
     * @return element found
     */
    public C get(K key) {
        return index.get(key);
    }
}
