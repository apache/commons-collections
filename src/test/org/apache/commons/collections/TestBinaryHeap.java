/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestBinaryHeap.java,v 1.5 2002/08/17 12:07:24 scolebourne Exp $
 * $Revision: 1.5 $
 * $Date: 2002/08/17 12:07:24 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

import junit.framework.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

/**
 * Tests the BinaryHeap.
 * 
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
 * @version $Id: TestBinaryHeap.java,v 1.5 2002/08/17 12:07:24 scolebourne Exp $
 */
public class TestBinaryHeap extends TestCollection {
    
  public static Test suite() {
    return new TestSuite(TestBinaryHeap.class);
  }
  
  public TestBinaryHeap(String testName) {
    super(testName);
  }
  
  /**
   * Return a new, empty {@link Object} to used for testing.
   */
  public Collection makeCollection() {
    return new BinaryHeap();
  }
  

  public Collection makeConfirmedCollection() {
    return new ArrayList();
  }

  public Collection makeConfirmedFullCollection() {
    ArrayList list = new ArrayList();
    list.addAll(Arrays.asList(getFullElements()));
    return list;
  }

  public Object[] getFullElements() {
      return getFullNonNullStringElements();
  }

  public Object[] getOtherElements() {
      return getOtherNonNullStringElements();
  }

  public void testCollectionIteratorFailFast() {
  }

  public void testBasicOps() {
    BinaryHeap heap = new BinaryHeap();
    
    assertTrue("heap should be empty after create", heap.isEmpty());
    
    try {
      heap.peek();
      fail("NoSuchElementException should be thrown if peek is called " +
           "before any elements are inserted");
    } catch (NoSuchElementException e) {
      // expected
    }
    
    try {
      heap.pop();
      fail("NoSuchElementException should be thrown if pop is called " +
           "before any elements are inserted");
    } catch (NoSuchElementException e) {
      // expected
    }
    
    heap.insert("a");
    heap.insert("c");
    heap.insert("e");
    heap.insert("b");
    heap.insert("d");
    heap.insert("n");
    heap.insert("m");
    heap.insert("l");
    heap.insert("k");
    heap.insert("j");
    heap.insert("i");
    heap.insert("h");
    heap.insert("g");
    heap.insert("f");
    
    assertTrue("heap should not be empty after inserts", !heap.isEmpty());
    
    for(int i = 0; i < 14; i++) {
      assertEquals("peek using default constructor should return " +
                   "minimum value in the binary heap", 
                   String.valueOf((char)('a' + i)), heap.peek());
      
      assertEquals("pop using default constructor should return minimum " +
                   "value in the binary heap", 
                   String.valueOf((char)('a' + i)), heap.pop());
      
      if(i + 1 < 14) {
        assertTrue("heap should not be empty before all elements are popped",
                   !heap.isEmpty());
      } else {
        assertTrue("heap should be empty after all elements are popped", 
                   heap.isEmpty());
      }
    }

    try {
      heap.peek();
      fail("NoSuchElementException should be thrown if peek is called " +
           "after all elements are popped");
    } catch (NoSuchElementException e) {
      // expected
    }
    
    try {
      heap.pop();
      fail("NoSuchElementException should be thrown if pop is called " +
           "after all elements are popped");
    } catch (NoSuchElementException e) {
      // expected
    }     
  }
  
  public void testBasicComparatorOps() {
    BinaryHeap heap = 
      new BinaryHeap(new ReverseComparator(new ComparableComparator()));
    
    assertTrue("heap should be empty after create", heap.isEmpty());
    
    try {
      heap.peek();
      fail("NoSuchElementException should be thrown if peek is called " +
           "before any elements are inserted");
    } catch (NoSuchElementException e) {
      // expected
    }
    
    try {
      heap.pop();
      fail("NoSuchElementException should be thrown if pop is called " +
           "before any elements are inserted");
    } catch (NoSuchElementException e) {
      // expected
    }
    
    heap.insert("a");
    heap.insert("c");
    heap.insert("e");
    heap.insert("b");
    heap.insert("d");
    heap.insert("n");
    heap.insert("m");
    heap.insert("l");
    heap.insert("k");
    heap.insert("j");
    heap.insert("i");
    heap.insert("h");
    heap.insert("g");
    heap.insert("f");
    
    assertTrue("heap should not be empty after inserts", !heap.isEmpty());
    
    for(int i = 0; i < 14; i++) {

      // note: since we're using a comparator that reverses items, the
      // "minimum" item is "n", and the "maximum" item is "a".

      assertEquals("peek using default constructor should return " +
                   "minimum value in the binary heap", 
                   String.valueOf((char)('n' - i)), heap.peek());
      
      assertEquals("pop using default constructor should return minimum " +
                   "value in the binary heap", 
                   String.valueOf((char)('n' - i)), heap.pop());
      
      if(i + 1 < 14) {
        assertTrue("heap should not be empty before all elements are popped",
                   !heap.isEmpty());
      } else {
        assertTrue("heap should be empty after all elements are popped", 
                   heap.isEmpty());
      }
    }

    try {
      heap.peek();
      fail("NoSuchElementException should be thrown if peek is called " +
           "after all elements are popped");
    } catch (NoSuchElementException e) {
      // expected
    }
    
    try {
      heap.pop();
      fail("NoSuchElementException should be thrown if pop is called " +
           "after all elements are popped");
    } catch (NoSuchElementException e) {
      // expected
    }     
  }


  public void verify() {
      super.verify();
      BinaryHeap heap = (BinaryHeap)collection;

      Comparator c = heap.comparator();
      if (c == null) c = ComparatorUtils.naturalComparator();
      if (!heap.m_isMinHeap) c = ComparatorUtils.reversedComparator(c);

      Object[] tree = heap.m_elements;
      for (int i = 1; i <= heap.m_size; i++) {
          Object parent = tree[i];
          if (i * 2 <= heap.m_size) {
              assertTrue("Parent is less than or equal to its left child", 
                c.compare(parent, tree[i * 2]) <= 0);
          }
          if (i * 2 + 1 < heap.m_size) {
              assertTrue("Parent is less than or equal to its right child", 
                c.compare(parent, tree[i * 2 + 1]) <= 0);
          }
      }
  }
}

