package org.apache.commons.collections;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Stack;

/**
 * This class implements a stack for String objects.
 *
 * @since 2.0
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id: StringStack.java,v 1.2 2002/06/12 03:59:15 mas Exp $
 */
public class StringStack implements Serializable
{
    /**
     * The stack of <code>String</code> objects.
     */
    private Stack stack = null;

    /**
     * Creates an empty instance.
     */
    public StringStack()
    {
        stack = new Stack();
    }

    /**
     * Adds the String to the collection if it does not already
     * contain it.
     *
     * @param s The <code>String</code> object to add to this stack
     * (if it is not <code>null</code> and doesn't already exist in
     * the stack).
     * @return A reference to this stack (useful for when this method
     * is called repeatedly).
     */
    public StringStack add(String s)
    {
        if (s != null && !contains(s))
        {
            stack.push(s);
        }
        return this;
    }

    /**
     * Adds all Strings in the given StringStack to the collection
     * (skipping those it already contains)
     *
     * @param ss The stack of <code>String</code> objects to add to
     * this stack (if it is not <code>null</code> and doesn't already
     * exist in the stack).
     * @return A reference to this stack (useful for when this method
     * is called repeatedly).
     */
    public StringStack addAll(StringStack ss)
    {
        Iterator i = ss.stack.iterator();
        while (i.hasNext())
        {
            add((String) i.next());
        }
        return this;
    }

    /**
     * Clears the stack.
     */
    public void clear()
    {
        stack.clear();
    }
    
    /**
     * Returns whether this stack contain the specified text.
     *
     * @param s The text to search for.
     * @return Whether the stack contains the text.
     */
    public boolean contains(String s)
    {
        return (stack.search(s) != -1);
    }

    /**
     * Whether the stack is empty.
     *
     * @return Whether the stack is empty.
     */
    public final boolean empty()
    {
        return stack.empty();
    }

    /**
     * Get a string off the stack at a certain position.
     *
     * @param i The position.
     * @return A the string from the specified position.
     */
    public String get(int i)
    {
        return (String) stack.elementAt(i);
    }

    /**
     * Returns the size of the stack.
     *
     * @return The size of the stack.
     */
    public final int size()
    {
        return stack.size();
    }

    /**
     * Converts the stack to a single {@link java.lang.String} with no
     * separator.
     *
     * @return The stack elements as a single block of text.
     */
    public String toString()
    {
        return toString("");
    }

    /**
     * Converts the stack to a single {@link java.lang.String}.
     *
     * @param separator The text to use as glue between elements in
     * the stack.
     * @return The stack elements--glued together by
     * <code>separator</code>--as a single block of text.
     */
    public String toString( String separator )
    {
        String s;
        if (size() > 0)
        {
            if ( separator == null )
            {
                separator = "";
            }
 
            // Determine what size to pre-allocate for the buffer.
            int totalSize = 0;
            for (int i = 0; i < stack.size(); i++)
            {
                totalSize += get(i).length();
            }
            totalSize += (stack.size() - 1) * separator.length();

            StringBuffer sb = new StringBuffer(totalSize).append( get(0) );
            for (int i = 1; i < stack.size(); i++)
            {
                sb.append(separator).append(get(i));
            }
            s = sb.toString();
        }
        else
        {
            s = "";
        }
        return s;
    }

    /**
     * Compares two StringStacks.  Considered equal if the
     * <code>toString()</code> method returns such.
     */
    public boolean equals(Object ssbuf)
    {
        boolean isEquiv = false;
        if ( ssbuf == null || !(ssbuf instanceof StringStack) ) 
        {
            isEquiv = false;
        }
        else if ( ssbuf == this ) 
        {
            isEquiv = true;
        }
        else if ( this.toString().equals(ssbuf.toString()) )
        {
            isEquiv = true;
        }
        return isEquiv;
    }

    /**
     * Turns this stack into an array.
     *
     * @return This stack as an array.
     */
    public String[] toStringArray()
    {
        String[] array = new String[size()];
        for (int i = 0; i < size(); i++)
        {
            array[i] = get(i);
        }
        return array;
    }
}
