/*
 * Copyright 2002-2004 The Apache Software Foundation
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

/**
 * Tests the BinaryHeap.
 * 
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
 * @version $Id: TestBinaryHeap.java,v 1.7.2.1 2004/05/22 12:14:05 scolebourne Exp $
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

      Comparator c = heap.m_comparator;
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

