/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/MultiMap.java,v 1.7 2003/08/31 17:26:43 scolebourne Exp $
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
 * This is simply a Map with slightly different semantics.
 * <p>
 * A <code>MultiMap</code> is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that
 * key. Getting a value will always return a Collection, holding all the
 * values put to that key. This implementation uses an ArrayList as the 
 * collection.
 * <p>
 * For example:
 * <pre>
 * MultiMap mhm = new MultiHashMap();
 * mhm.put(key, "A");
 * mhm.put(key, "B");
 * mhm.put(key, "C");
 * Collection coll = (Collection) mhm.get(key);</pre>
 * <p>
 * <code>coll</code> will be a list containing "A", "B", "C".
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.7 $ $Date: 2003/08/31 17:26:43 $
 * 
 * @author Christopher Berry
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Stephen Colebourne
 */
public interface MultiMap extends Map {
    
    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * 
     * @param key  the key to remove from
     * @param value  the value to remove
     * @return the value removed (which was passed in)
     */
    public Object remove(Object key, Object item);
   
}
