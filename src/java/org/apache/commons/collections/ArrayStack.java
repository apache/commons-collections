/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/ArrayStack.java,v 1.1 2001/04/14 19:32:37 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 19:32:37 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


import java.util.ArrayList;
import java.util.EmptyStackException;


/**
 * An implementation of the {@link java.util.Stack} API that is based on an
 * <code>ArrayList</code> instead of a <code>Vector</code>, so it is not
 * synchronized to protect against multi-threaded access.  The implementation
 * is therefore operates faster in environments where you do not need to
 * worry about multiple thread contention.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2001/04/14 19:32:37 $
 * @see java.util.Stack
 */

public class ArrayStack extends ArrayList {


    // --------------------------------------------------------- Public Methods


    /**
     * Return <code>true</code> if this stack is currently empty.
     */
    public boolean empty() {

        return (size() == 0);

    }


    /**
     * Return the top item off of this stack without removing it.
     *
     * @exception EmptyStackExceptino if the stack is empty
     */
    public Object peek() throws EmptyStackException {

        int n = size();
        if (n <= 0)
            throw new EmptyStackException();
        else
            return (get(n - 1));

    }


    /**
     * Pop the top item off of this stack and return it.
     *
     * @exception EmptyStackException if the stack is empty
     */
    public Object pop() throws EmptyStackException {

        int n = size();
        if (n <= 0)
            throw new EmptyStackException();
        else
            return (remove(n - 1));

    }


    /**
     * Push a new item onto the top of this stack.  The pushed item is also
     * returned.
     *
     * @param item Item to be added
     */
    public Object push(Object item) {

        if (item == null)
            throw new NullPointerException();
        add(item);
        return (item);

    }


    /**
     * Return the one-based position of the distance from the top that the
     * specified object exists on this stack, where the top-most element is
     * considered to be at distance <code>1</code>.  If the object is not
     * present on the stack, return <code>-1</code> instead.  The
     * <code>equals()</code> method is used to compare to the items
     * in this stack.
     *
     * @param o Object to be searched for
     */
    public int search(Object o) {

        int i = size() - 1;        // Current index
        int n = 1;                 // Current distance
        while (i >= 0) {
            if (o.equals(get(i)))
                return (n);
            i--;
            n++;
        }
        return (-1);

    }


}
