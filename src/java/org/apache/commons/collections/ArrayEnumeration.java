/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Enumeration wrapper for array.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
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
        m_elements = elements;
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

