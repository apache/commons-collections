/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 */
package org.apache.commons.collections;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Runtime exception thrown from functors.
 * If required, a root cause error can be wrapped within this one.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/01/14 21:43:03 $
 *
 * @author Stephen Colebourne
 */
public class FunctorException extends RuntimeException {
    
    /**
     * Does JDK support nested exceptions
     */
    private static final boolean JDK_SUPPORTS_NESTED;
    
    static {
        boolean flag = false;
        try {
            Throwable.class.getDeclaredMethod("getCause", new Class[0]);
            flag = true;
        } catch (NoSuchMethodException ex) {
            flag = false;
        }
        JDK_SUPPORTS_NESTED = flag;
    }
    
    /**
     * Root cause of the exception
     */
    private final Throwable rootCause;

    /**
     * Constructs a new <code>FunctorException</code> without specified
     * detail message.
     */
    public FunctorException() {
        super();
        this.rootCause = null;
    }

    /**
     * Constructs a new <code>FunctorException</code> with specified
     * detail message.
     *
     * @param msg  the error message.
     */
    public FunctorException(String msg) {
        super(msg);
        this.rootCause = null;
    }

    /**
     * Constructs a new <code>FunctorException</code> with specified
     * nested <code>Throwable</code> root cause.
     *
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public FunctorException(Throwable rootCause) {
        super((rootCause == null ? null : rootCause.getMessage()));
        this.rootCause = rootCause;
    }

    /**
     * Constructs a new <code>FunctorException</code> with specified
     * detail message and nested <code>Throwable</code> root cause.
     *
     * @param msg        the error message.
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public FunctorException(String msg, Throwable rootCause) {
        super(msg);
        this.rootCause = rootCause;
    }

    /**
     * Gets the cause of this throwable.
     * 
     * @return  the cause of this throwable, or <code>null</code>
     */
    public Throwable getCause() {
        return rootCause;
    }

    /**
     * Prints the stack trace of this exception to the standard error stream.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace of this exception to the specified stream.
     *
     * @param out  the <code>PrintStream</code> to use for output
     */
    public void printStackTrace(PrintStream out) {
        synchronized (out) {
            PrintWriter pw = new PrintWriter(out, false);
            printStackTrace(pw);
            // Flush the PrintWriter before it's GC'ed.
            pw.flush();
        }
    }

    /**
     * Prints the stack trace of this exception to the specified writer.
     *
     * @param out  the <code>PrintWriter</code> to use for output
     */
    public void printStackTrace(PrintWriter out) {
        synchronized (out) {
            super.printStackTrace(out);
            if (rootCause != null && JDK_SUPPORTS_NESTED == false) {
                out.print("Caused by: ");
                rootCause.printStackTrace(out);
            }
        }
    }

}
