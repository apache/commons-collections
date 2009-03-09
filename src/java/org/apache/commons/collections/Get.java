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
import java.util.Map;
import java.util.Set;

/**
 * The "read" subset of the {@link Map} interface.
 * @since Commons Collections 5
 * @TODO fix version
 * @version $Revision$ $Date$
 * @see Put
 * @author Matt Benson
 */
public interface Get<K, V> {

    /**
     * @see Map#containsKey(Object)
     */
    public boolean containsKey(Object key);

    /**
     * @see Map#containsValue(Object)
     */
    public boolean containsValue(Object value);

    /**
     * @see Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, V>> entrySet();

    /**
     * @see Map#get(Object)
     */
    public V get(Object key);

    /**
     * @see Map#remove(Object)
     */
    public V remove(Object key);

    /**
     * @see Map#isEmpty()
     */
    public boolean isEmpty();

    /**
     * @see Map#keySet()
     */
    public Set<K> keySet();

    /**
     * @see Map#size()
     */
    public int size();

    /**
     * @see Map#values()
     */
    public Collection<V> values();
}
