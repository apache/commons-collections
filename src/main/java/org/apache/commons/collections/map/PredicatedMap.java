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
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.Predicate;

/**
 * Decorates another <code>Map</code> to validate that additions
 * match a specified predicate.
 * <p>
 * This map exists to provide validation for the decorated map.
 * It is normally created to decorate an empty map.
 * If an object cannot be added to the map, an IllegalArgumentException is thrown.
 * <p>
 * One usage would be to ensure that no null keys are added to the map.
 * <pre>Map map = PredicatedSet.decorate(new HashMap(), NotNullPredicate.INSTANCE, null);</pre>
 * <p>
 * <strong>Note that PredicatedMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedMap(Map)}. This class may throw 
 * exceptions when accessed by concurrent threads without synchronization.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class PredicatedMap<K, V>
        extends AbstractInputCheckedMapDecorator<K, V>
        implements Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 7412622456128415156L;

    /** The key predicate to use */
    protected final Predicate<? super K> keyPredicate;

    /** The value predicate to use */
    protected final Predicate<? super V> valuePredicate;

    /**
     * Factory method to create a predicated (validating) map.
     * <p>
     * If there are any elements already in the list being decorated, they
     * are validated.
     * 
     * @param map  the map to decorate, must not be null
     * @param keyPredicate  the predicate to validate the keys, null means no check
     * @param valuePredicate  the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    public static <K, V> PredicatedMap<K, V> predicatedMap(Map<K, V> map,
                                                           Predicate<? super K> keyPredicate,
                                                           Predicate<? super V> valuePredicate) {
        return new PredicatedMap<K, V>(map, keyPredicate, valuePredicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @param keyPredicate  the predicate to validate the keys, null means no check
     * @param valuePredicate  the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    protected PredicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        super(map);
        this.keyPredicate = keyPredicate;
        this.valuePredicate = valuePredicate;
        
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            validate(entry.getKey(), entry.getValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     * 
     * @param out  the output stream
     * @throws IOException
     * @since Commons Collections 3.1
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     * 
     * @param in  the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     * @since Commons Collections 3.1
     */
    @SuppressWarnings("unchecked") // (1) should only fail if input stream is incorrect 
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map<K, V>) in.readObject(); // (1)
    }

    //-----------------------------------------------------------------------
    /**
     * Validates a key value pair.
     * 
     * @param key  the key to validate
     * @param value  the value to validate
     * @throws IllegalArgumentException if invalid
     */
    protected void validate(K key, V value) {
        if (keyPredicate != null && keyPredicate.evaluate(key) == false) {
            throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
        }
        if (valuePredicate != null && valuePredicate.evaluate(value) == false) {
            throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
        }
    }

    /**
     * Override to validate an object set into the map via <code>setValue</code>.
     * 
     * @param value  the value to validate
     * @throws IllegalArgumentException if invalid
     * @since Commons Collections 3.1
     */
    @Override
    protected V checkSetValue(V value) {
        if (valuePredicate.evaluate(value) == false) {
            throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
        }
        return value;
    }

    /**
     * Override to only return true when there is a value transformer.
     * 
     * @return true if a value predicate is in use
     * @since Commons Collections 3.1
     */
    @Override
    protected boolean isSetValueChecking() {
        return (valuePredicate != null);
    }

    //-----------------------------------------------------------------------
    @Override
    public V put(K key, V value) {
        validate(key, value);
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        for (Map.Entry<? extends K, ? extends V> entry : mapToCopy.entrySet()) {
            validate(entry.getKey(), entry.getValue());
        }
        super.putAll(mapToCopy);
    }

}
