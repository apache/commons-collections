/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/ProxyMap.java,v 1.5 2002/08/15 20:04:31 pjack Exp $
 * $Revision: 1.5 $
 * $Date: 2002/08/15 20:04:31 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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


