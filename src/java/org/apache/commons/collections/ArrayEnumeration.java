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

import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Enumeration wrapper for array.
 * 
 * @since 1.0
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated This class has significant overlap with ArrayIterator,
 *             and Collections focuses mainly on Java2-style
 *             collections.  If you need to enumerate an array,
 *             create an {@link ArrayIterator} and wrap it with an
 *             {@link IteratorEnumeration} instead.
 */
public final class ArrayEnumeration
    implements Enumeration
{
    protected Object[]       m_elements;
    protected int            m_index;

    public ArrayEnumeration( final List elements )
    {
        m_elements = elements.toArray();
    }

    public ArrayEnumeration( final Object[] elements )
    {
        if(elements == null) {
            m_elements = new Object[0];
        } else {
            m_elements = elements;
        }
    }

    public boolean hasMoreElements()
    {
        return ( m_index < m_elements.length );
    }

    public Object nextElement()
    {
        if( !hasMoreElements() )
        {
            throw new NoSuchElementException("No more elements exist");
        }

        return m_elements[ m_index++ ];
    }
}

