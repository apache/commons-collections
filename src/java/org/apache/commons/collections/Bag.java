/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Bag.java,v 1.5 2002/03/13 06:00:20 mas Exp $
 * $Revision: 1.5 $
 * $Date: 2002/03/13 06:00:20 $
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
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Collection} that counts the number of times an object appears in
 * the collection.  Suppose you have a Bag that contains <code>{a, a, b,
 * c}</code>.  Calling {@link #getCount(Object)} on <code>a</code> would return
 * 2, while calling {@link #uniqueSet()} would return <code>{a, b, c}</code>.
 *
 * @author Chuck Burdick
 **/
public interface Bag extends Collection {
   /**
    * Return the number of occurrences (cardinality) of the given
    * object currently in the bag. If the object does not exist in the
    * bag, return 0.
    **/
   public int getCount(Object o);

   /**
    * Add the given object to the bag and keep a count. If the object
    * is already in the {@link #uniqueSet()} then increment its count as
    * reported by {@link #getCount(Object)}. Otherwise add it to the {@link
    * #uniqueSet()} and report its count as 1.
    * @return <code>true</code> if the object was not already in the
    *         <code>uniqueSet</code>
    * @see #getCount(Object)
    **/
   public boolean add(Object o);

   /**
    * Add <code>i</code> copies of the given object to the bag and
    * keep a count.
    * @return <code>true</code> if the object was not already in the
    *         <code>uniqueSet</code>
    * @see #add(Object)
    * @see #getCount(Object)
    **/
   public boolean add(Object o, int i);

   /**
    * Remove all occurrences of the given object from the bag, and do
    * not represent the object in the {@link #uniqueSet()}.
    * @see #remove(Object, int)
    * @return <code>true</code> if this call changed the collection
    **/
   public boolean remove(Object o);

   /**
    * Remove the given number of occurrences from the bag. If the bag
    * contains <code>i</code> occurrences or less, the item will be
    * removed from the {@link #uniqueSet()}.
    * @see #getCount(Object)
    * @see #remove(Object)
    * @return <code>true</code> if this call changed the collection
    **/
   public boolean remove(Object o, int i);

   /**
    * The {@link Set} of unique members that represent all members in
    * the bag. Uniqueness constraints are the same as those in {@link
    * Set}.
    **/
   public Set uniqueSet();

   /**
    * Returns the total number of items in the bag across all types.
    **/
   public int size();

   /**
    * Returns <code>true</code> if the bag contains all elements in
    * the given collection, respecting cardinality.  That is, if the
    * given collection <code>C</code> contains <code>n</code> copies
    * of a given object, calling {@link #getCount(Object)} on that object must
    * be <code>&gt;= n</code> for all <code>n</code> in <code>C</code>.
    **/
   public boolean containsAll(Collection c);

   /**
    * Remove all elements represented in the given collection,
    * respecting cardinality.  That is, if the given collection
    * <code>C</code> contains <code>n</code> copies of a given object,
    * the bag will have <code>n</code> fewer copies, assuming the bag
    * had at least <code>n</code> copies to begin with.
    * @return <code>true</code> if this call changed the collection
    **/
   public boolean removeAll(Collection c);

   /**
    * Remove any members of the bag that are not in the given
    * collection, respecting cardinality.  That is, if the given
    * collection <code>C</code> contains <code>n</code> copies of a
    * given object and the bag has <code>m &gt; n</code> copies, then
    * delete <code>m - n</code> copies from the bag.  In addition, if
    * <code>e</code> is an object in the bag but
    * <code>!C.contains(e)</code>, then remove <code>e</code> and any
    * of its copies.
    *
    * @return <code>true</code> if this call changed the collection
    **/
   public boolean retainAll(Collection c);

   /**
    * Returns an {@link Iterator} over the entire set of members,
    * including copies due to cardinality. This iterator is fail-fast
    * and will not tolerate concurrent modifications.
    **/
   public Iterator iterator();
}





