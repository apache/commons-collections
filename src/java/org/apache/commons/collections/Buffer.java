/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author Avalon
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @author Paul Jack
 * @author Stephen Colebourne
 * @version $Id: Buffer.java,v 1.3.2.1 2004/05/22 12:14:01 scolebourne Exp $
 * @since 2.1
 */
public interface Buffer extends Collection {

    /**
     * Removes the next object from the buffer.
     *
     * @return  the removed object
     * @throws BufferUnderflowException if the buffer is already empty
     */
    Object remove();

    /**
     * Returns the next object in the buffer without removing it.
     *
     * @return  the next object in the buffer
     * @throws BufferUnderflowException if the buffer is empty
     */
    Object get();
}
