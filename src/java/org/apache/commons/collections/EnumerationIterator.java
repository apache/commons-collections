/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/EnumerationIterator.java,v 1.3 2002/02/10 08:07:42 jstrachan Exp $
 * $Revision: 1.3 $
 * $Date: 2002/02/10 08:07:42 $
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
import java.util.Enumeration;
import java.util.Iterator;

/** Adapter to make {@link Enumeration Enumeration} instances appear
  * to be {@link Iterator Iterator} instances.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
  */
public class EnumerationIterator implements Iterator {
    
    private Collection collection;

    private Enumeration enumeration;

    private Object last;
    
    public EnumerationIterator() {
        this(null, null);
    }
    
    public EnumerationIterator( Enumeration enumeration ) {
        this(enumeration, null);
    }

    public EnumerationIterator( Enumeration enum, Collection collection ) {
        this.enumeration = enum;
        this.collection = collection;
        this.last = null;
    }

    // Iterator interface
    //-------------------------------------------------------------------------
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    public Object next() {
        last = enumeration.nextElement();
        return last;
    }

    /**
     * Functions if an associated <code>Collection</code> is known.
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
    public Enumeration getEnumeration() {
        return enumeration;
    }
    
    public void setEnumeration( Enumeration enumeration ) {
        this.enumeration = enumeration;
    }
}
