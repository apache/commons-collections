/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/03 01:44:04 $
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
