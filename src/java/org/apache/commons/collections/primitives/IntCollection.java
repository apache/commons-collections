/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/IntCollection.java,v 1.1 2003/01/04 15:00:57 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/04 15:00:57 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

package org.apache.commons.collections.primitives;

/**
 * A {@link java.util.Collection collection} of int values.
 *
 * @version $Revision: 1.1 $ $Date: 2003/01/04 15:00:57 $
 * @author Rodney Waldhoff 
 */
public interface IntCollection {
          
    /** 
     * Ensures that I contain the specified element 
     * (optional operation).
     */
    boolean add(int element);

    /** 
     * {@link #add Adds} all of the elements in the 
     * specified collection to me 
     * (optional operation). 
     */ 
    boolean addAll(IntCollection c);
    
    /** 
     * Removes all my elements 
     * (optional operation). 
     */
    void clear();

    /** 
     * Returns <code>true</code> iff I contain 
     * the specified element. 
     */
    boolean contains(int element);
    
    /** 
     * Returns <code>true</code> iff I contain 
     * all of the elements in the given collection. 
     */
    boolean containsAll(IntCollection c);
    
    /** 
     * Compares the specified object with me for 
     * equality. 
     */
    boolean equals(Object o);
    
    /** 
     * Returns my hash code value. 
     */
    int hashCode();
    
    /** 
     * Returns true iff I contains no elements. 
     */
    boolean isEmpty();
    
    /** 
     * Returns an iterator over all my elements.
     */
    IntIterator iterator();
     
    /** 
     * Removes the first occurrence of the 
     * specified element (optional operation). 
     */
    boolean removeElement(int element);
    
    /** 
     * Removes from all the elements that are 
     * contained in the specified collection 
     * (optional operation). 
     */
    boolean removeAll(IntCollection c);
    
    /** 
     * Retains only the elements that are 
     * contained in the specified collection 
     * (optional operation). 
     */
    boolean retainAll(IntCollection c);
    
    /** 
     * Returns the number of elements I contain. 
     */
    int size();
    
    /** 
     * Returns an array containing all my elements.
     */
    int[] toArray();
    
    /** 
     * Returns an array containing all of the elements 
     * in me, using the given array if it is large enough.
     */
    int[] toArray(int[] a);
}
