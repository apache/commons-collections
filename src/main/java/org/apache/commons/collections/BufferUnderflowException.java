/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.NoSuchElementException;

/**
 * The BufferUnderflowException is used when the buffer is already empty.
 * <p>
 * NOTE: From version 3.0, this exception extends NoSuchElementException.
 *
 * @since 2.1
 * @version $Revision$
 *
 * @author Avalon
 * @author Berin Loritsch
 * @author Jeff Turner
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public class BufferUnderflowException extends NoSuchElementException {
    
    /** Serialization version */
    private static final long serialVersionUID = 7106567570467436893L;

    /**
     * Constructs a new <code>BufferUnderflowException</code>.
     */
    public BufferUnderflowException() {
        super();
    }

    /** 
     * Construct a new <code>BufferUnderflowException</code>.
     * 
     * @param message  the detail message for this exception
     */
    public BufferUnderflowException(String message) {
        super(message);
    }

    /** 
     * Construct a new <code>BufferUnderflowException</code>.
     * 
     * @param message  the detail message for this exception
     * @param exception  the root cause of the exception
     */
    public BufferUnderflowException(String message, Throwable exception) {
        super(message);
        initCause(exception);
    }

}
