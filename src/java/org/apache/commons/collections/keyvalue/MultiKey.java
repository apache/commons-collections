/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.keyvalue;

import java.io.Serializable;
import java.util.Arrays;

/** 
 * A <code>MultiKey</code> allows multiple map keys to be merged together.
 * <p>
 * The purpose of this class is to avoid the need to write code to handle
 * maps of maps. An example might be the need to lookup a filename by 
 * key and locale. The typical solution might be nested maps. This class
 * can be used instead by creating an instance passing in the key and locale.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2004/02/18 01:00:08 $
 * 
 * @author Howard Lewis Ship
 * @author Stephen Colebourne
 */
public class MultiKey implements Serializable {

    /** Serialisation version */
    private static final long serialVersionUID = 4465448607415788805L;

    /** The individual keys */
    private final Object[] keys;
    /** The cached hashCode */
    private final int hashCode;
    
    /**
     * Constructor taking two keys.
     * 
     * @param key1  the first key
     * @param key2  the second key
     */
    public MultiKey(Object key1, Object key2) {
        this(new Object[] {key1, key2}, false);
    }
    
    /**
     * Constructor taking three keys.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     */
    public MultiKey(Object key1, Object key2, Object key3) {
        this(new Object[] {key1, key2, key3}, false);
    }
    
    /**
     * Constructor taking four keys.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     */
    public MultiKey(Object key1, Object key2, Object key3, Object key4) {
        this(new Object[] {key1, key2, key3, key4}, false);
    }
    
    /**
     * Constructor taking five keys.
     * 
     * @param key1  the first key
     * @param key2  the second key
     * @param key3  the third key
     * @param key4  the fourth key
     * @param key5  the fifth key
     */
    public MultiKey(Object key1, Object key2, Object key3, Object key4, Object key5) {
        this(new Object[] {key1, key2, key3, key4, key5}, false);
    }
    
    /**
     * Constructor taking an array of keys.
     *
     * @param keys  the array of keys
     * @throws IllegalArgumentException if the key array is null
     */
    public MultiKey(Object[] keys) {
        this(keys, true);
    }
    
    /**
     * Constructor taking an array of keys.
     * <p>
     * If the array is not copied, then it must not be modified.
     *
     * @param keys  the array of keys
     * @param makeCopy  true to copy the array, false to assign it
     * @throws IllegalArgumentException if the key array is null
     */
    protected MultiKey(Object[] keys, boolean makeCopy) {
        super();
        if (keys == null) {
            throw new IllegalArgumentException("The array of keys must not be null");
        }
        if (makeCopy) {
            this.keys = (Object[]) keys.clone();
        } else {
            this.keys = keys;
        }
        
        int total = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) {
                if (i == 0) {
                    total = keys[i].hashCode();
                } else {
                    total ^= keys[i].hashCode();
                }
            }
        }
        hashCode = total;
    }
    
    /**
     * Gets a copy of the individual keys.
     * 
     * @return the individual keys
     */
    public Object[] getKeys() {
        return (Object[]) keys.clone();
    }
    
    /**
     * Compares this object to another.
     * <p>
     * To be equal, the other object must be a <code>MultiKey</code> with the
     * same number of keys which are also equal.
     * 
     * @param other  the other object to compare to
     * @return true if equal
     */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultiKey) {
            MultiKey otherMulti = (MultiKey) other;
            return Arrays.equals(keys, otherMulti.keys);
        }
        return false;
    }

    /**
     * Gets the combined hash code that is computed from all the keys.
     * <p>
     * This value is computed once and then cached, so elements should not
     * change their hash codes once created (note that this is the same 
     * constraint that would be used if the individual keys elements were
     * themselves {@link java.util.Map Map} keys.
     * 
     * @return the hash code
     */
    public int hashCode() {
        return hashCode;
    }

    /**
     * Gets a debugging string version of the key.
     * 
     * @return a debugging string
     */
    public String toString() {
        return "MultiKey" + Arrays.asList(keys).toString();
    }

}
