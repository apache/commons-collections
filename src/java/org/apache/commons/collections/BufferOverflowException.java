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

/**
 * The BufferOverflowException is used when the buffer's capacity has been
 * exceeded.
 *
 * @author Avalon
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 * @author Paul Jack
 * @author Stephen Colebourne
 * @since 2.1
 * @version $Id: BufferOverflowException.java,v 1.4.2.1 2004/05/22 12:14:02 scolebourne Exp $
 */
public class BufferOverflowException extends RuntimeException {
    private final Throwable m_throwable;

    /**
     * Constructs a new <code>BufferOverflowException</code>.
     */
    public BufferOverflowException() {
        super();
        m_throwable = null;
    }

    /** 
     * Construct a new <code>BufferOverflowException</code>.
     * 
     * @param message  the detail message for this exception
     */
    public BufferOverflowException(String message) {
        this(message, null);
    }

    /** 
     * Construct a new <code>BufferOverflowException</code>.
     * 
     * @param message  the detail message for this exception
     * @param throwable  the root cause of the exception
     */
    public BufferOverflowException(String message, Throwable exception) {
        super(message);
        m_throwable = exception;
    }

    /**
     * Gets the root cause of the exception.
     *
     * @return the root cause
     */
    public final Throwable getCause() {
        return m_throwable;
    }
}
