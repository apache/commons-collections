/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/FloatCollection.java,v 1.2 2003/08/31 17:21:15 scolebourne Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

package org.apache.commons.collections.primitives;

/**
 * A collection of <code>float</code> values.
 *
 * @see org.apache.commons.collections.primitives.adapters.FloatCollectionCollection
 * @see org.apache.commons.collections.primitives.adapters.CollectionFloatCollection
 *
 * @since Commons Collections 2.2
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:21:15 $
 * 
 * @author Rodney Waldhoff 
 */
public interface FloatCollection {
    /** 
     * Ensures that I contain the specified element 
     * (optional operation).  Returns <code>true</code>
     * iff I changed as a result of this call.
     * <p/>
     * If a collection refuses to add the specified
     * element for any reason other than that it already contains
     * the element, it <i>must</i> throw an exception (rather than
     * simply returning <tt>false</tt>).  This preserves the invariant
     * that a collection always contains the specified element after 
     * this call returns. 
     * 
     * @param element the value whose presence within me is to be ensured
     * @return <code>true</code> iff I changed as a result of this call
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     * @throws IllegalArgumentException may be thrown if some aspect of the 
     *         specified element prevents it from being added to me
     */
    boolean add(float element);

    /** 
     * {@link #add Adds} all of the elements in the 
     * specified collection to me (optional operation). 
     * 
     * @param c the collection of elements whose presence within me is to 
     *        be ensured
     * @return <code>true</code> iff I changed as a result of this call
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     * @throws IllegalArgumentException may be thrown if some aspect of some 
     *         specified element prevents it from being added to me
     */ 
    boolean addAll(FloatCollection c);
    
    /** 
     * Removes all my elements (optional operation). 
     * I will be {@link #isEmpty empty} after this
     * method successfully returns.
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     */
    void clear();

    /** 
     * Returns <code>true</code> iff I contain 
     * the specified element. 
     * 
     * @param element the value whose presence within me is to be tested
     * @return <code>true</code> iff I contain the specified element
     */
    boolean contains(float element);
    
    /** 
     * Returns <code>true</code> iff I {@link #contains contain}
     * all of the elements in the given collection.
     * 
     * @param c the collection of elements whose presence within me is to 
     *        be tested
     * @return <code>true</code> iff I contain the all the specified elements
     */
    boolean containsAll(FloatCollection c);
    
    /** 
     * Returns <code>true</code> iff I contain no elements. 
     * @return <code>true</code> iff I contain no elements. 
     */
    boolean isEmpty();
    
    /** 
     * Returns an {@link FloatIterator iterator} over all my elements.
     * This base interface places no constraints on the order 
     * in which the elements are returned by the returned iterator.
     * @return an {@link FloatIterator iterator} over all my elements.
     */
    FloatIterator iterator();
    
    /** 
     * Removes all of my elements that are contained in the 
     * specified collection (optional operation). 
     * The behavior of this method is unspecified if 
     * the given collection is modified while this method
     * is executing.  Note that this includes the case
     * in which the given collection is this collection, 
     * and it is not empty.
     * 
     * @param c the collection of elements to remove
     * @return <code>true</code> iff I contained the at least one of the
     *         specified elements, in other words, returns <code>true</code>
     *         iff I changed as a result of this call
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     */
    boolean removeAll(FloatCollection c);
     
    /** 
     * Removes a single occurrence of the specified element 
     * (optional operation). 
     * 
     * @param element the element to remove, if present
     * @return <code>true</code> iff I contained the specified element, 
     *         in other words, iff I changed as a result of this call
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     */
    boolean removeElement(float element);
    
    /** 
     * Removes all of my elements that are <i>not</i> contained in the 
     * specified collection (optional operation). 
     * (In other words, retains <i>only</i> my elements that are 
     * contained in the specified collection.)
     * The behavior of this method is unspecified if 
     * the given collection is modified while this method
     * is executing.
     * 
     * @param c the collection of elements to retain
     * @return <code>true</code> iff I changed as a result 
     *         of this call
     * 
     * @throws UnsupportedOperationException when this operation is not 
     *         supported
     */
    boolean retainAll(FloatCollection c);
    
    /** 
     * Returns the number of elements I contain. 
     * @return the number of elements I contain
     */
    int size();
    
    /** 
     * Returns an array containing all of my elements.
     * The length of the returned array will be equal
     * to my {@link #size size}.
     * <p/>
     * The returned array will be independent of me, 
     * so that callers may modify that 
     * returned array without modifying this collection.
     * <p/>
     * When I guarantee the order in which 
     * elements are returned by an {@link #iterator iterator},
     * the returned array will contain elements in the
     * same order.
     * 
     * @return an array containing all my elements
     */
    float[] toArray();
    
    /** 
     * Returns an array containing all of my elements, 
     * using the given array if it is large 
     * enough.  When the length of the given array is 
     * larger than the number of elements I contain, 
     * values outside of my range will be unchanged.
     * <p/>
     * The returned array will be independent of me, 
     * so that callers may modify that 
     * returned array without modifying this collection.
     * <p/>
     * When I guarantee the order in which 
     * elements are returned by an {@link #iterator iterator},
     * the returned array will contain elements in the
     * same order.
     * 
     * @param a an array that may be used to contain the elements
     * @return an array containing all my elements
     */
    float[] toArray(float[] a);
}
