/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/iterators/EnumerationIterator.java,v 1.1 2002/08/15 23:13:51 pjack Exp $
 * $Revision: 1.1 $
 * $Date: 2002/08/15 23:13:51 $
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
package org.apache.commons.collections.iterators;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/** Adapter to make {@link Enumeration Enumeration} instances appear
  * to be {@link Iterator Iterator} instances.
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
  */
public class EnumerationIterator implements Iterator {
    
    private Collection collection;

    private Enumeration enumeration;

    private Object last;
    
    /**
     *  Constructs a new <Code>EnumerationIterator</Code> that will not
     *  function until {@link #setEnumeration(Enumeration)} is called.
     */
    public EnumerationIterator() {
        this(null, null);
    }

    /**
     *  Constructs a new <Code>EnumerationIterator</Code> that provides
     *  an iterator view of the given enumeration.
     *
     *  @param enumeration  the enumeration to use
     */
    public EnumerationIterator( Enumeration enumeration ) {
        this(enumeration, null);
    }

    /**
     *  Constructs a new <Code>EnumerationIterator</Code> that will remove
     *  elements from the specified collection.
     *
     *  @param enum  the enumeration to use
     *  @param collection  the collection to remove elements form
     */
    public EnumerationIterator( Enumeration enum, Collection collection ) {
        this.enumeration = enum;
        this.collection = collection;
        this.last = null;
    }

    // Iterator interface
    //-------------------------------------------------------------------------

    /**
     *  Returns true if the underlying enumeration has more elements.
     *
     *  @return true if the underlying enumeration has more elements
     *  @throws NullPointerException  if the underlying enumeration is null
     */
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    /**
     *  Returns the next object from the enumeration.
     *
     *  @return the next object from the enumeration
     *  @throws NullPointerException if the enumeration is null
     */
    public Object next() {
        last = enumeration.nextElement();
        return last;
    }

    /**
     * Functions if an associated <code>Collection</code> is known.
     * If so, the first occurrence of the last returned object from this
     * iterator will be removed from the collection.
     *
     * @exception IllegalStateException <code>next()</code> not called.
     * @exception UnsupportedOperationException No associated
     * <code>Collection</code>.
     */
    public void remove() {
        if (collection != null) {
            if (last != null) {
                collection.remove(last);
            }
            else {
                throw new IllegalStateException
                    ("next() must have been called for remove() to function");
            }
        }
        else {
            throw new UnsupportedOperationException
                ("No Collection associated with this Iterator");
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     *  Returns the underlying enumeration.
     *
     *  @return the underlying enumeration
     */
    public Enumeration getEnumeration() {
        return enumeration;
    }

    /**
     *  Sets the underlying enumeration.
     *
     *  @param enumeration  the new underlying enumeration
     */
    public void setEnumeration( Enumeration enumeration ) {
        this.enumeration = enumeration;
    }
}
