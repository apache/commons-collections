/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BidiMap.java,v 1.1 2003/09/20 20:24:30 scolebourne Exp $
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
package org.apache.commons.collections;

import java.util.Map;

/**
 * Defines a map that allows bidirectional lookup between key and values.
 * <p>
 * Implementations should allow a value to be looked up from a key and
 * a key to be looked up from a value with equal performance.
 *  
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/09/20 20:24:30 $
 *
 * @author Stephen Colebourne
 */
public interface BidiMap extends Map {
    
    /**
     * Gets the key that is currently mapped to the specified value.
     * <p>
     * If the value is not contained in the map, <code>null</code> is returned.
     * <p>
     * Implementations should seek to make this method perform equally as well
     * as <code>get(Object)</code>.
     *
     * @param value  the value to find the key for
     * @return the mapped key, or <code>null</code> if not found
     * 
     * @throws ClassCastException (optional) if the map limits the type of the 
     *  value and the specifed value is inappropriate
     * @throws NullPointerException (optional) if the map limits the values to
     *  non-null and null was specified
     */
    public Object getKey(Object value);
    
    /**
     * Removes the key-value pair that is currently mapped to the specified
     * value (optional operation).
     * <p>
     * If the value is not contained in the map, <code>null</code> is returned.
     * <p>
     * Implementations should seek to make this method perform equally as well
     * as <code>remove(Object)</code>.
     *
     * @param value  the value to find the key-value pair for
     * @return the key that was removed, <code>null</code> if nothing removed
     * 
     * @throws ClassCastException (optional) if the map limits the type of the 
     *  value and the specifed value is inappropriate
     * @throws NullPointerException (optional) if the map limits the values to
     *  non-null and null was specified
     * @throws UnsupportedOperationException if this method is not supported
     *  by the implementation
     */
    public Object removeKey(Object value);
    
    /**
     * Gets a view of this map where the keys and values are reversed.
     * <p>
     * Changes to one map will be visible in the other and vice versa.
     * This enables both directions of the map to be accessed as a <code>Map</code>.
     * <p>
     * Implementations should seek to avoid creating a new object every time this
     * method is called. See <code>AbstractMap.values()</code> etc. Calling this
     * method on the inverse map should return the original.
     *
     * @return an inverted bidirectional map
     */
    public BidiMap inverseBidiMap();
    
}
