/*
 * Copyright 2001-2004 The Apache Software Foundation
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
import java.io.Serializable;
import java.util.Iterator;
import java.util.Stack;
/**
 * This class implements a stack for String objects.
 * <p>
 * This class provides a way to collect a list of unique strings and join
 * them with an optional separator.
 * 
 * @deprecated This class is not a Stack, it is a String utility. As such
 * it is deprecated in favour of the <code>StringUtils</code> class in 
 * the <code>[lang]</code> project.
 * @since 2.0
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author Stephen Colebourne
 * @version $Id: StringStack.java,v 1.3.2.1 2004/05/22 12:14:01 scolebourne Exp $
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
