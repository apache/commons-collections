/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/AbstractLongArrayList.java,v 1.10 2003/08/31 17:21:15 scolebourne Exp $
 * ====================================================================
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

package org.apache.commons.collections.primitives;



/**
 * Abstract base class for lists of primitive <Code>long</Code> elements
 * backed by an array.<P>
 *
 * Extending this class is essentially the same as extending its superclass
 * ({@link AbstractLongList}.  However, this class assumes that the 
 * primitive values will be stored in an underlying primitive array, and
 * provides methods for manipulating the capacity of that array.<P>
 *
 * @version $Revision: 1.10 $ $Date: 2003/08/31 17:21:15 $
 * @author Rodney Waldhoff
 *  
 * @deprecated A {@link LongList} implementation, such as {@link ArrayLongList} 
 *             should be used instead.  Use {@link LongListList} for {@link List} 
 *             compatibility.
 */
public abstract class AbstractLongArrayList extends AbstractLongList {

    //------------------------------------------------------ Abstract Accessors
    
    /**
     *  Returns the maximum size the list can reach before the array 
     *  is resized.
     *
     *  @return the maximum size the list can reach before the array is resized
     */
    abstract public int capacity();

    //------------------------------------------------------ Abstract Modifiers

    /**
     *  Ensures that the length of the internal <Code>long</Code> array is
     *  at list the given value.
     *
     *  @param mincap  the minimum capacity for this list
     */
    abstract public void ensureCapacity(int mincap);

    /**
     *  Resizes the internal array such that {@link #capacity()} is equal
     *  to {@link #size()}.
     */
    abstract public void trimToSize();

}
