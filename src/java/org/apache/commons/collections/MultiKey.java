/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/MultiKey.java,v 1.4 2003/08/31 17:26:44 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 *
 */
package org.apache.commons.collections;

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
 * @version $Revision: 1.4 $ $Date: 2003/08/31 17:26:44 $
 * 
 * @author Howard Lewis Ship
 * @author Stephen Colebourne
 */
public class MultiKey implements Serializable {

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
     * Gets the combined hashcode that is computed from all the keys.
     * <p>
     * This value is computed once and then cached, so elements should not
     * change their hash codes once created (note that this is the same 
     * constraint that would be used if the individual keys elements were
     * themselves {@link java.util.Map Map} keys.
     * 
     * @return the hashcode
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
