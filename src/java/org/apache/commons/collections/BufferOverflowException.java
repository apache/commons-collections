/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.commons.collections;

/**
 * The BufferOverflowException is used when the buffer's capacity has been
 * exceeded.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 */
public class BufferOverflowException extends RuntimeException
{
    private final Throwable m_throwable;

    /** Construct a new BufferOverflowException.
     * @param message The detail message for this exception.
     */
    public BufferOverflowException( String message )
    {
        this( message, null );
    }

    /** Construct a new BufferOverflowException.
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public BufferOverflowException( String message, Throwable exception )
    {
        super( message );
        m_throwable = exception;
    }

    /**
     * Retrieve root cause of the exception.
     *
     * @return the root cause
     */
    public final Throwable getCause()
    {
        return m_throwable;
    }
}
