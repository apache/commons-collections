/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A case-insensitive <code>Map</code>.
 * <p>
 * As entries are added to the map, keys are converted to all lowercase. A new 
 * key is compared to existing keys by comparing <code>newKey.toString().toLower()</code>
 * to the lowercase values in the current <code>KeySet.</code>
 * <p>
 * Null keys are supported.  
 * <p>
 * The <code>keySet()</code> method returns all lowercase keys, or nulls.
 * <p>
 * Example:
 * <pre><code>
 *  Map map = new CaseInsensitiveMap();
 *  map.put("One", "One");
 *  map.put("Two", "Two");
 *  map.put(null, "Three");
 *  map.put("one", "Four");
 * </code></pre>
 * creates a <code>CaseInsensitiveMap</code> with three entries.<br>
 * <code>map.get(null)</code> returns <code>"Three"</code> and <code>map.get("ONE")</code>
 * returns <code>"Four".</code>  The <code>Set</code> returned by <code>keySet()</code>
 * equals <code>{"one", "two", null}.</code>
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/01/14 21:43:10 $
 *
 * @author Commons-Collections team
 */
public class CaseInsensitiveMap extends AbstractHashedMap implements Serializable, Cloneable {

    /** Serialisation version */
    private static final long serialVersionUID = -7074655917369299456L;

    /**
     * Constructs a new empty map with default size and load factor.
     */
    public CaseInsensitiveMap() {
        super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity. 
     *
     * @param initialCapacity  the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than one
     */
    public CaseInsensitiveMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor. 
     *
     * @param initialCapacity  the initial capacity
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the initial capacity is less than one
     * @throws IllegalArgumentException if the load factor is less than zero
     */
    public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor copying elements from another map.
     * <p>
     * Keys will be converted to lower case strings, which may cause
     * some entries to be removed (if string representation of keys differ
     * only by character case).
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     */
    public CaseInsensitiveMap(Map map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Overrides convertKey() from {@link AbstractHashedMap} to convert keys to 
     * lower case.
     * <p>
     * Returns null if key is null.
     * 
     * @param key  the key convert
     * @return the converted key
     */
    protected Object convertKey(Object key) {
        if (key != null) {
            return key.toString().toLowerCase();
        } else {
            return AbstractHashedMap.NULL;
        }
    }   

    //-----------------------------------------------------------------------
    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    public Object clone() {
        return super.clone();
    }

    /**
     * Write the map out using a custom routine.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    /**
     * Read the map in using a custom routine.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }
 
}
