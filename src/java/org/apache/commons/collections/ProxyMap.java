/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 * <p>This <code>Map</code> wraps another <code>Map</code>
 * implementation, using the wrapped instance for its default
 * implementation.  This class is used as a framework on which to
 * build to extensions for its wrapped <code>Map</code> object which
 * would be unavailable or inconvenient via sub-classing (but usable
 * via composition).</p>
 *
 * <p>An example use case is where the wrapped <code>Map</code> needs
 * synchronization (to make it thread-safe), but the <code>Map</code>
 * returned by <code>Collections.synchronizedMap(map)</code>
 * hides part of <code>map</code>'s public interface.</p>
 *
 * @since 2.0
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 */
public abstract class ProxyMap implements Map {
    
    /**
     * The <code>Map</code> used for default implementations.
     */
    protected Map map;

    /**
     * Creates a new instance acting as a representative for the
     * specified <code>Map</code>.
     *
     * @param map The <code>Map</code> to whose operations to wrap.
     */
    public ProxyMap(Map map) {
        this.map = map;
    }

    /**
     *  Invokes the underlying {@link Map#clear()} method.
     */
    public void clear() {
        map.clear();
    }

    /**
     *  Invokes the underlying {@link Map#containsKey(Object)} method.
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     *  Invokes the underlying {@link Map#containsValue(Object)} method.
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     *  Invokes the underlying {@link Map#entrySet()} method.
     */
    public Set entrySet() {
        return map.entrySet();
    }

    /**
     *  Invokes the underlying {@link Map#equals(Object)} method.
     */
    public boolean equals(Object m) {
        return map.equals(m);
    }

    /**
     *  Invokes the underlying {@link Map#get(Object)} method.
     */
    public Object get(Object key) {
        return map.get(key);
    }

    /**
     *  Invokes the underlying {@link Map#hashCode()} method.
     */
    public int hashCode() {
        return map.hashCode();
    }

    /**
     *  Invokes the underlying {@link Map#isEmpty()} method.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     *  Invokes the underlying {@link Map#keySet()} method.
     */
    public Set keySet() {
        return map.keySet();
    }

    /**
     *  Invokes the underlying {@link Map#put(Object,Object)} method.
     */
    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    /**
     *  Invokes the underlying {@link Map#putAll(Map)} method.
     */
    public void putAll(Map t) {
        map.putAll(t);
    }

    /**
     *  Invokes the underlying {@link Map#remove(Object)} method.
     */
    public Object remove(Object key) {
        return map.remove(key);
    }

    /**
     *  Invokes the underlying {@link Map#size()} method.
     */
    public int size() {
        return map.size();
    }

    /**
     *  Invokes the underlying {@link Map#values()} method.
     */
    public Collection values() {
        return map.values();
    }
   
}


