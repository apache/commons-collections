/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Bag.java,v 1.11 2003/08/31 17:26:44 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link Collection} that counts the number of times an object appears in
 * the collection.
 * <p>
 * Suppose you have a Bag that contains <code>{a, a, b, c}</code>.
 * Calling {@link #getCount(Object)} on <code>a</code> would return 2, while
 * calling {@link #uniqueSet()} would return <code>{a, b, c}</code>.
 * <p>
 * <i>Note that this interface violates the {@link Collection} contract.</i> 
 * The behavior specified in many of these methods is <I>not</I> the same
 * as the behavior specified by {@link Collection}.  The noncompliant methods
 * are clearly marked with "(Violation)".  A future
 * version of this class will specify the same behavior as {@link Collection},
 * which unfortunately will break backwards compatibility with this version.
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.11 $ $Date: 2003/08/31 17:26:44 $
 * 
 * @author Chuck Burdick
 * @author Stephen Colebourne
 */
public interface Bag extends Collection {

    /**
     * Returns the number of occurrences (cardinality) of the given
     * object currently in the bag. If the object does not exist in the
     * bag, return 0.
     * 
     * @param object  the object to search for
     * @return the number of occurrences of the object, zero if not found
     */
    int getCount(Object object);

    /**
     * <i>(Violation)</i>
     * Adds one copy the specified object to the Bag.
     * <p>
     * If the object is already in the {@link #uniqueSet()} then increment its
     * count as reported by {@link #getCount(Object)}. Otherwise add it to the
     * {@link #uniqueSet()} and report its count as 1.
     * <p>
     * Since this method always increases the size of the bag,
     * according to the {@link Collection#add(Object)} contract, it 
     * should always return <code>true</code>.  Since it sometimes returns
     * <code>false</code>, this method violates the contract.  A future
     * version of this method will comply by always returning <code>true</code>.
     *
     * @param object  the object to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    boolean add(Object object);

    /**
     * Adds <code>nCopies</code> copies of the specified object to the Bag.
     * <p>
     * If the object is already in the {@link #uniqueSet()} then increment its
     * count as reported by {@link #getCount(Object)}. Otherwise add it to the
     * {@link #uniqueSet()} and report its count as <code>nCopies</code>.
     * 
     * @param object  the object to add
     * @param nCopies  the number of copies to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    boolean add(Object object, int nCopies);

    /**
     * <i>(Violation)</i>
     * Removes all occurrences of the given object from the bag.
     * <p>
     * This will also remove the object from the {@link #uniqueSet()}.
     * <p>
     * According to the {@link Collection#remove(Object)} method,
     * this method should only remove the <i>first</i> occurrence of the
     * given object, not <i>all</i> occurrences.  A future version of this
     * method will comply with the contract by only removing one occurrence
     * of the given object.
     *
     * @return <code>true</code> if this call changed the collection
     */
    boolean remove(Object object);

    /**
     * Removes <code>nCopies</code> copies of the specified object from the Bag.
     * <p>
     * If the number of copies to remove is greater than the actual number of
     * copies in the Bag, no error is thrown.
     * 
     * @param object  the object to remove
     * @param nCopies  the number of copies to remove
     * @return <code>true</code> if this call changed the collection
     */
    boolean remove(Object object, int nCopies);

    /**
     * Returns a {@link Set} of unique elements in the Bag.
     * <p>
     * Uniqueness constraints are the same as those in {@link java.util.Set}.
     * 
     * @return the Set of unique Bag elements
     */
    Set uniqueSet();

    /**
     * Returns the total number of items in the bag across all types.
     * 
     * @return the total size of the Bag
     */
    int size();

    /**
     * <i>(Violation)</i>
     * Returns <code>true</code> if the bag contains all elements in
     * the given collection, respecting cardinality.  That is, if the
     * given collection <code>coll</code> contains <code>n</code> copies
     * of a given object, calling {@link #getCount(Object)} on that object must
     * be <code>&gt;= n</code> for all <code>n</code> in <code>coll</code>.
     * <p>
     * The {@link Collection#containsAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * return true if the bag contains at least one of every object contained
     * in the given collection.  A future version of this method will comply
     * with that contract.
     * 
     * @param coll  the collection to check against
     * @return <code>true</code> if the Bag contains all the collection
     */
    boolean containsAll(Collection coll);

    /**
     * <i>(Violation)</i>
     * Remove all elements represented in the given collection,
     * respecting cardinality.  That is, if the given collection
     * <code>coll</code> contains <code>n</code> copies of a given object,
     * the bag will have <code>n</code> fewer copies, assuming the bag
     * had at least <code>n</code> copies to begin with.
     *
     * <P>The {@link Collection#removeAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * remove <i>all</i> occurrences of every object contained in the 
     * given collection.  A future version of this method will comply
     * with that contract.
     *
     * @param coll  the collection to remove
     * @return <code>true</code> if this call changed the collection
     */
    boolean removeAll(Collection coll);

    /**
     * <i>(Violation)</i>
     * Remove any members of the bag that are not in the given
     * collection, respecting cardinality.  That is, if the given
     * collection <code>coll</code> contains <code>n</code> copies of a
     * given object and the bag has <code>m &gt; n</code> copies, then
     * delete <code>m - n</code> copies from the bag.  In addition, if
     * <code>e</code> is an object in the bag but
     * <code>!coll.contains(e)</code>, then remove <code>e</code> and any
     * of its copies.
     *
     * <P>The {@link Collection#retainAll(Collection)} method specifies
     * that cardinality should <i>not</i> be respected; this method should
     * keep <i>all</i> occurrences of every object contained in the 
     * given collection.  A future version of this method will comply
     * with that contract.
     *
     * @param coll  the collection to retain
     * @return <code>true</code> if this call changed the collection
     */
    boolean retainAll(Collection coll);

    /**
     * Returns an {@link Iterator} over the entire set of members,
     * including copies due to cardinality. This iterator is fail-fast
     * and will not tolerate concurrent modifications.
     * 
     * @return iterator over all elements in the Bag
     */
    Iterator iterator();

}
