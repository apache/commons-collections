/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/HashBidiMap.java,v 1.2 2003/09/26 23:28:43 matth Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of <code>BidiMap</code>.
 * 
 * @since Commons Collections 3.0
 * @version $Id: HashBidiMap.java,v 1.2 2003/09/26 23:28:43 matth Exp $
 * 
 * @author Matthew Hawthorne
 */
public class HashBidiMap extends AbstractMap implements BidiMap, Serializable {

    /**
     * Delegate map array.  The first map contains standard entries, and the 
     * second contains inverses.
     */
    final Map[] maps = new Map[] { new HashMap(), new HashMap()};

    /**
     * Inverse view of this map.
     */
    private final BidiMap inverseBidiMap = new InverseBidiMap();

    /**
     * Creates an empty <code>HashBidiMap</code>
     */
    public HashBidiMap() {}

    /** 
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  
     *
     * @param   m the map whose mappings are to be placed in this map.
     */
    public HashBidiMap(Map m) {
        putAll(m);
    }

    public Object getKey(Object value) {
        return maps[1].get(value);
    }

    public BidiMap inverseBidiMap() {
        return inverseBidiMap;
    }

    public Object removeKey(Object value) {
        final Object key = maps[1].get(value);
        return remove(key);
    }

    public Object put(Object key, Object value) {
        // Removes pair from standard map if a previous inverse entry exists
        final Object oldValue = maps[1].put(value, key);
        if (oldValue != null) {
            maps[0].remove(oldValue);
        }
        
        final Object obj = maps[0].put(key, value);
        return obj;
    }

    public Set entrySet() {
        // The entrySet is the root of most Map methods, care must be taken not 
        // to reference instance methods like size()

        // Creates anonymous AbstractSet
        return new AbstractSet() {

            public Iterator iterator() {
                // Creates anonymous Iterator
                return new Iterator() {

                    // Delegate iterator.
                    final Iterator it = maps[0].entrySet().iterator();

                    // Current iterator entry
                    Map.Entry currentEntry;

                    public void remove() {
                        // Removes from standard and inverse Maps.

                        // Object must be removed using the iterator or a 
                        // ConcurrentModificationException is thrown
                        it.remove();
                        HashBidiMap.this.maps[1].remove(
                            currentEntry.getValue());
                    }

                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    public Object next() {
                        currentEntry = (Map.Entry)it.next();
                        return currentEntry;
                    };
                }; // anonymous Iterator
            }

            public boolean remove(Object obj) {
                // XXX Throws ClassCastException if obj is not a Map.Entry.
                // Is this acceptable?
                final Object removed =
                    HashBidiMap.this.remove(((Map.Entry)obj).getKey());
                return removed != null;
            }

            public int size() {
                return HashBidiMap.this.maps[0].size();
            }

        }; // anonymous AbstractSet

    } // entrySet()

    /**
     * Inverse view of this BidiMap.
     */
    private final class InverseBidiMap extends AbstractMap implements BidiMap {

        public Object getKey(Object value) {
            return HashBidiMap.this.get(value);
        }

        public BidiMap inverseBidiMap() {
            return HashBidiMap.this;
        }

        public Object removeKey(Object value) {
            return HashBidiMap.this.remove(value);
        }

        public Set entrySet() {
            // Gets entry set from outer class
            final Set entrySet = HashBidiMap.this.entrySet();

            // Returns anonymous Set
            return new AbstractSet() {

                public int size() {
                    return HashBidiMap.this.size();
                }

                public Iterator iterator() {
                    final Iterator delegate = entrySet.iterator();

                    // Returns anonymous Iterator
                    return new Iterator() {

                        public boolean hasNext() {
                            return delegate.hasNext();
                        }

                        public Object next() {
                            final Map.Entry entry = (Map.Entry)delegate.next();
                            return new DefaultMapEntry(
                                entry.getValue(),
                                entry.getKey());
                        }

                        public void remove() {
                            delegate.remove();
                        }

                    }; // anonymous Iterator
                }

            }; // anonymous AbstractSet

        } // entrySet()

    } // InverseBidiMap

} // HashBidiMap
