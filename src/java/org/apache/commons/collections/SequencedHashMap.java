/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/SequencedHashMap.java,v 1.2 2002/02/10 08:07:42 jstrachan Exp $
 * $Revision: 1.2 $
 * $Date: 2002/02/10 08:07:42 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A {@link java.util.HashMap} whose keys are sequenced.  The
 * sequencing of the keys allow easy access to the values in the order
 * which they were added in.  This class is thread safe.</p>
 *
 * <p>Implementing the List interface is not possible due to a instance
 * method name clash between the Collection and the List interface:
 *
 * <table>
 * <tr><td>Collections</td><td>boolean remove(Object o)</td></tr>
 * <tr><td>Lists</td><td>Object remove(Object o)</td></tr>
 * </table>
 * </p>
 *
 * <p>So one cannot implement both interfaces at the same, which is
 * unfortunate because the List interface would be very nice in
 * conjuction with <a
 * href="http://jakarta.apache.org/velocity/">Velocity</a>.</p>
 *
 * <p>A slightly more complex implementation and interface could involve
 * the use of a list of <code>Map.Entry</code> objects.</p>
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public class SequencedHashMap extends HashMap
{
    /**
     * The index of the eldest element in the collection.
     */
    protected static final int ELDEST_INDEX = 0;

    /**
     * Indicator for an unknown index.
     */
    private static final int UNKNOWN_INDEX = -1;

    /**
     * The sequence used to keep track of the hash keys.  Younger objects are
     * kept towards the end of the list.  Does not allow duplicates.
     */
    private LinkedList keySequence;

    /**
     * Creates a new instance with default storage.
     */
    public SequencedHashMap ()
    {
        keySequence = new LinkedList();
    }

    /**
     * Creates a new instance with the specified storage.
     *
     * @param size The storage to allocate up front.
     */
    public SequencedHashMap (int size)
    {
        super(size);
        keySequence = new LinkedList();
    }

    /**
     * Clears all elements.
     */
    public void clear ()
    {
        super.clear();
        keySequence.clear();
    }

    /**
     * Creates a shallow copy of this object, preserving the internal
     * structure by copying only references.  The keys, values, and
     * sequence are not <code>clone()</code>'d.
     *
     * @return A clone of this instance.
     */
    public Object clone ()
    {
        SequencedHashMap seqHash = (SequencedHashMap) super.clone();
        seqHash.keySequence = (LinkedList) keySequence.clone();
        return seqHash;
    }

    /**
     * Returns the key at the specified index.
     */
    public Object get (int index)
    {
        return keySequence.get(index);
    }

    /**
     * Returns the value at the specified index.
     */
    public Object getValue (int index)
    {
        return get(get(index));
    }

    /**
     * Returns the index of the specified key.
     */
    public int indexOf (Object key)
    {
        return keySequence.indexOf(key);
    }

    /**
     * Returns a key iterator.
     */
    public Iterator iterator ()
    {
        return keySequence.iterator();
    }

    /**
     * Returns the last index of the specified key.
     */
    public int lastIndexOf (Object key)
    {
        return keySequence.lastIndexOf(key);
    }

    /**
     * Returns the ordered sequence of keys.
     *
     * This method is meant to be used for retrieval of Key / Value pairs
     * in e.g. Velocity:
     * <PRE>
     * ## $table contains a sequenced hashtable
     * #foreach ($key in $table.sequence())
     * &lt;TR&gt;
     * &lt;TD&gt;Key: $key&lt;/TD&gt;
     * &lt;/TD&gt;Value: $table.get($key)&lt;/TD&gt;
     * &lt;/TR&gt;
     * #end
     * </PRE>
     *
     * @return The ordered list of keys.
     */
    public List sequence()
    {
        return keySequence;
    }

    /**
     * Stores the provided key/value pair.  Freshens the sequence of existing
     * elements.
     *
     * @param key   The key to the provided value.
     * @param value The value to store.
     * @return      The previous value for the specified key, or
     *              <code>null</code> if none.
     */
    public Object put (Object key, Object value)
    {
        Object prevValue = super.put(key, value);
        freshenSequence(key, prevValue);
        return prevValue;
    }

    /**
     * Freshens the sequence of the element <code>value</code> if
     * <code>value</code> is not <code>null</code>.
     *
     * @param key   The key whose sequence to freshen.
     * @param value The value whose existance to check before removing the old
     *              key sequence.
     */
    protected void freshenSequence(Object key, Object value)
    {
        if (value != null)
        {
            // Freshening existing element's sequence.
            keySequence.remove(key);
        }
        keySequence.add(key);
    }

    /**
     * Stores the provided key/value pairs.
     *
     * @param t The key/value pairs to store.
     */
    public void putAll (Map t)
    {
        Set set = t.entrySet();
        for (Iterator iter = set.iterator(); iter.hasNext(); )
        {
            Map.Entry e = (Map.Entry)iter.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index The index of the object to remove.
     * @return      The previous value coressponding the <code>key</code>, or
     *              <code>null</code> if none existed.
     */
    public Object remove (int index)
    {
        return remove(index, null);
    }

    /**
     * Removes the element with the specified key.
     *
     * @param key   The <code>Map</code> key of the object to remove.
     * @return      The previous value coressponding the <code>key</code>, or
     *              <code>null</code> if none existed.
     */
    public Object remove (Object key)
    {
        return remove(UNKNOWN_INDEX, key);
    }

    /**
     * Removes the element with the specified key or index.
     *
     * @param index The index of the object to remove, or
     *              <code>UNKNOWN_INDEX</code> if not known.
     * @param key   The <code>Map</code> key of the object to remove.
     * @return      The previous value coressponding the <code>key</code>, or
     *              <code>null</code> if none existed.
     */
    private final Object remove (int index, Object key)
    {
        if (index == UNKNOWN_INDEX)
        {
            index = indexOf(key);
        }
        if (key == null)
        {
            key = get(index);
        }
        if (index != UNKNOWN_INDEX)
        {
            keySequence.remove(index);
        }
        return super.remove(key);
    }
}

