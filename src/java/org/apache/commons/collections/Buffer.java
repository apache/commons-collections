/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Buffer.java,v 1.2 2002/07/03 01:45:47 mas Exp $
 * $Revision: 1.2 $
 * $Date: 2002/07/03 01:45:47 $
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


/**
 * A Buffer is a collection that allows objects to be removed in some
 * well-defined order.  The removal order can be based on insertion order
 * (eg, a FIFO queue or a LIFO stack), on access order (eg, an LRU cache), 
 * on some arbitrary comparator (eg, a priority queue) or on any other 
 * well-defined ordering.<P>
 *
 * Note that the removal order is not necessarily the same as the iteration
 * order.  A <Code>Buffer</Code> implementation may have equivalent removal
 * and iteration orders, but this is not required.<P>
 *
 * This interface does not specify any behavior for 
 * {@link Object#equals(Object)} and {@link Object#hashCode} methods.  It
 * is therefore possible for a <Code>Buffer</Code> implementation to also
 * also implement {@link java.util.List}, {@link java.util.Set} or 
 * {@link Bag}.
 *
 * @author  <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/07/03 01:45:47 $
 * @since Avalon 4.0
 */
public interface Buffer extends Collection
{

    /**
     * Removes the next object from the buffer.
     *
     * @return  the removed object
     * @throws BufferUnderflowException if the buffer is already empty
     */
    Object remove();



    /**
     *  Returns the next object in the buffer without removing it.
     *
     *  @return  the next object in the buffer
     *  @throws BufferUnderflowException if the buffer is empty
     */
    Object get();
}
