/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/Attic/MapIterator.java,v 1.1 2003/11/02 15:27:54 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.Map;

/**
 * Defines an iterator that operates over a <code>Map</code>.
 * <p>
 * This iterator is a special version designed for maps. It is much more
 * efficient to use this rather than an entry set iterator where the option
 * is available.
 * <p>
 * A map that provides this interface may not hold the data internally using
 * Map Entry objects, thus this interface can avoid lots of object creation.
 * <p>
 * In use, this iterator iterates through the keys in the map. After each call
 * to <code>next()</code>, the <code>getValue()</code> method provides direct
 * access to the value. The value can also be set using <code>setValue()</code>.
 * <pre>
 * MapIterator it = map.mapIterator();
 * while (it.hasNext()) {
 *   Object key = it.next();
 *   Object value = it.getValue();
 *   it.setValue(newValue);
 * }
 * </pre>
 *  
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/02 15:27:54 $
 *
 * @author Stephen Colebourne
 */
public interface MapIterator extends Iterator {
    
    /**
     * Checks to see if there are more entries still to be iterated.
     *
     * @return <code>true</code> if the iterator has more elements
     */
    boolean hasNext();

    /**
     * Gets the next <em>key</em> from the <code>Map</code>.
     *
     * @return the next key in the iteration
     * @throws NoSuchElementException if the iteration is finished
     */
    Object next();

    //-----------------------------------------------------------------------
    /**
     * Gets the current key, which is the key returned by the last call
     * to <code>next()</code>.
     *
     * @return the current key
     * @throws IllegalStateException if <code>next()</code> has not yet been called
     */
    Object getKey();

    /**
     * Gets the current value, which is the value associated with the last key
     * returned by <code>next()</code>.
     *
     * @return the current value
     * @throws IllegalStateException if <code>next()</code> has not yet been called
     */
    Object getValue();

    //-----------------------------------------------------------------------
    /**
     * Gets the last returned key-value pair from the underlying <code>Map</code>
     * as a Map Entry instance.
     * <p>
     * The returned entry must not change when <code>next</code> is called.
     * Changes made to the entry via <code>setValue</code> must change the map.
     * 
     * @return the last return key-value pair as an independent Map Entry
     * @throws IllegalStateException if <code>next()</code> has not yet been called
     * @throws IllegalStateException if <code>remove()</code> has been called since the
     *  last call to <code>next()</code>
     */
    Map.Entry asMapEntry();
    
    //-----------------------------------------------------------------------
    /**
     * Removes the last returned key from the underlying <code>Map</code> (optional operation).
     * <p>
     * This method can be called once per call to <code>next()</code>.
     *
     * @throws UnsupportedOperationException if remove is not supported by the map
     * @throws IllegalStateException if <code>next()</code> has not yet been called
     * @throws IllegalStateException if <code>remove()</code> has already been called
     *  since the last call to <code>next()</code>
     */
    void remove();
    
    /**
     * Sets the value associated with the current key (optional operation).
     *
     * @param value  the new value
     * @return the previous value
     * @throws UnsupportedOperationException if setValue is not supported by the map
     * @throws IllegalStateException if <code>next()</code> has not yet been called
     * @throws IllegalStateException if <code>remove()</code> has been called since the
     *  last call to <code>next()</code>
     */
    Object setValue(Object value);

}
