/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/Attic/IntList.java,v 1.1 2003/01/04 15:00:57 rwaldhoff Exp $
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
 * An ordered collection (a {@link java.util.List}) of int values.
 *
 * @version $Revision: 1.1 $ $Date: 2003/01/04 15:00:57 $
 * @author Rodney Waldhoff 
 */
public interface IntList {
    /** 
     * Inserts the specified element at the specified position 
     * within me (optional operation). 
     */
    void add(int index, int element);
          
    /** 
     * Returns the element at the specified position within 
     * me. 
     */
    Object get(int index);
    
    /** 
     * Returns the index of the first occurrence 
     * of the specified element within me, 
     * or <code>-1</code> if I do not contain 
     * the this element. 
     */
    int indexOf(int element);
     
    /** 
     * Returns the index of the last occurrence 
     * of the specified element within me, 
     * or -1 if I do not contain this element. 
     */
    int lastIndexOf(int element);
    
    /** 
     * Returns a list iterator over all my elements.
     */
    IntListIterator listIterator();
    
    /** 
     * Returns a list iterator over my elements,
     * starting at the specified position. The 
     * specified index indicates the first element 
     * that would be returned by an initial call 
     * to the next method. An initial call to the 
     * previous method would return the element 
     * with the specified index minus one.
     */
    IntListIterator listIterator(int index);
    
    /** 
     * Removes the element at the specified position in 
     * me (optional operation). 
     */
    int removeElementAt(int index);
   
    /** 
     * Replaces the element at the specified 
     * position in me with the specified element
     * (optional operation). 
     */
    int set(int index, int element);
    
    /** 
     * Returns a view of the elements within me 
     * between the specified fromIndex, inclusive, and 
     * toIndex, exclusive. 
     */
    IntList subList(int fromIndex, int toIndex);

}
