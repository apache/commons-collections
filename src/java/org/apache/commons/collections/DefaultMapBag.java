/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/DefaultMapBag.java,v 1.5 2002/08/15 20:04:31 pjack Exp $
 * $Revision: 1.5 $
 * $Date: 2002/08/15 20:04:31 $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class provides a skeletal implementation of the {@link Bag}
 * interface to minimize the effort required for target implementations.
 * Subclasses need only to call {@link #setMap(Map)} in their constructor 
 * specifying a map instance that will be used to store the contents of 
 * the bag.<P>
 *
 * The map will be used to map bag elements to a number; the number represents
 * the number of occurrences of that element in the bag.<P>
 *
 * @since 2.0
 * @author Chuck Burdick
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
 **/
public abstract class DefaultMapBag implements Bag {
   private Map _map = null;
   private int _total = 0;
   private int _mods = 0;


   /**
    *  Constructor.  Subclasses should invoke {@link #setMap(Map)} in
    *  their constructors.
    */
   public DefaultMapBag() {
   }

   /**
    *  Adds a new element to the bag by incrementing its count in the 
    *  underlying map.
    *
    *  @see Bag#add(Object) 
    */
   public boolean add(Object o) {
      return add(o, 1);
   }

   /**
    *  Adds a new element to the bag by incrementing its count in the map.
    *
    *  @see Bag#add(Object, int)
    */
   public boolean add(Object o, int i) {
      _mods++;
      if (i > 0) {
         int count = (i + getCount(o));
         _map.put(o, new Integer(count));
         _total += i;
         return (count == i);
      } else {
         return false;
      }
   }

   /**
    *  Invokes {@link #add(Object)} for each element in the given collection.
    *
    *  @see Bag#addAll(Collection)
    */
   public boolean addAll(Collection c) {
      boolean changed = false;
      Iterator i = c.iterator();
      while (i.hasNext()) {
         boolean added = add(i.next());
         changed = changed || added;
      }
      return changed;
   }


   /**
    *  Clears the bag by clearing the underlying map.
    */
   public void clear() {
      _mods++;
      _map.clear();
      _total = 0;
   }

   /**
    *  Determines if the bag contains the given element by checking if the
    *  underlying map contains the element as a key.
    *
    *  @return true if the bag contains the given element
    */
   public boolean contains(Object o) {
      return _map.containsKey(o);
   }

   public boolean containsAll(Collection c) {
      return containsAll(new HashBag(c));
   }

   /**
    * Returns <code>true</code> if the bag contains all elements in
    * the given collection, respecting cardinality.
    * @see #containsAll(Collection)
    **/
   public boolean containsAll(Bag other) {
      boolean result = true;
      Iterator i = other.uniqueSet().iterator();
      while (i.hasNext()) {
         Object current = i.next();
         boolean contains =
            getCount(current) >= ((Bag)other).getCount(current);
         result = result && contains;
      }
      return result;
   }

   /**
    * Returns true if the given object is not null, has the precise type 
    * of this bag, and contains the same number of occurrences of all the
    * same elements.
    *
    * @param o the object to test for equality
    * @return true if that object equals this bag
    */
   public boolean equals(Object o) {
      return (o == this || 
              (o != null && o.getClass().equals(this.getClass()) &&
               ((DefaultMapBag)o)._map.equals(this._map)));
   }

   /**
    * Returns the hash code of the underlying map.
    *
    * @return the hash code of the underlying map
    */
   public int hashCode() {
      return _map.hashCode();
   }

   /**
    * Returns true if the underlying map is empty.
    *
    * @return true if there are no elements in this bag
    */
   public boolean isEmpty() {
      return _map.isEmpty();
   }

   public Iterator iterator() {
      return new BagIterator(this, extractList().iterator()); 
   }

   private class BagIterator implements Iterator {
      private DefaultMapBag _parent = null;
      private Iterator _support = null;
      private Object _current = null;
      private int _mods = 0;

      public BagIterator(DefaultMapBag parent, Iterator support) {
         _parent = parent;
         _support = support;
         _current = null;
         _mods = parent.modCount();
      }

      public boolean hasNext() {
         return _support.hasNext();
      }

      public Object next() {
         if (_parent.modCount() != _mods) {
            throw new ConcurrentModificationException();
         }
         _current = _support.next();
         return _current;
      }
      
      public void remove() {
         if (_parent.modCount() != _mods) {
            throw new ConcurrentModificationException();
         }
         _support.remove();
         _parent.remove(_current, 1);
         _mods++;
      }
   }

   public boolean remove (Object o) {
      return remove(o, getCount(o));
   }

   public boolean remove (Object o, int i) {
      _mods++;
      boolean result = false;
      int count = getCount(o);
      if (i <= 0) {
         result = false;
      } else if (count > i) {
         _map.put(o, new Integer(count - i));
         result = true;
         _total -= i;
      } else { // count > 0 && count <= i  
         // need to remove all
         result = (_map.remove(o) != null);
         _total -= count;
      }
      return result;
   }

   public boolean removeAll(Collection c) {
      boolean result = false;
      if (c != null) {
         Iterator i = c.iterator();
         while (i.hasNext()) {
            boolean changed = remove(i.next(), 1);
            result = result || changed;
         }
      }
      return result;
   }

   /**
    * Remove any members of the bag that are not in the given
    * bag, respecting cardinality.
    *
    * @return true if this call changed the collection
    */
   public boolean retainAll(Collection c) {
      return retainAll(new HashBag(c));
   }

   /**
    * Remove any members of the bag that are not in the given
    * bag, respecting cardinality.
    * @see #retainAll(Collection)
    * @return <code>true</code> if this call changed the collection
    **/
   public boolean retainAll(Bag other) {
      boolean result = false;
      Bag excess = new HashBag();
      Iterator i = uniqueSet().iterator();
      while (i.hasNext()) {
         Object current = i.next();
         int myCount = getCount(current);
         int otherCount = other.getCount(current);
         if (1 <= otherCount && otherCount <= myCount) {
            excess.add(current, myCount - otherCount);
         } else {
            excess.add(current, myCount);
         }
      }
      if (!excess.isEmpty()) {
         result = removeAll(excess);
      }
      return result;
   }

   /**
    *  Returns an array of all of this bag's elements.
    *
    *  @return an array of all of this bag's elements
    */
   public Object[] toArray() {
      return extractList().toArray();
   }

   /**
    *  Returns an array of all of this bag's elements.
    *
    *  @param a  the array to populate
    *  @return an array of all of this bag's elements
    */
   public Object[] toArray(Object[] a) {
      return extractList().toArray(a);
   }

   /**
    *  Returns the number of occurrence of the given element in this bag
    *  by looking up its count in the underlying map.
    *
    *  @see Bag#getCount(Object)
    */
   public int getCount(Object o) {
      int result = 0;
      Integer count = MapUtils.getInteger(_map, o);
      if (count != null) {
         result = count.intValue();
      }
      return result;
   }

   /**
    *  Returns an unmodifiable view of the underlying map's key set.
    *
    *  @return the set of unique elements in this bag
    */
   public Set uniqueSet() {
      return Collections.unmodifiableSet(_map.keySet());
   }

   /**
    *  Returns the number of elements in this bag.
    *
    *  @return the number of elements in this bag
    */
   public int size() {
      return _total;
   }

   /**
    * Actually walks the bag to make sure the count is correct and
    * resets the running total
    **/
   protected int calcTotalSize() {
      _total = extractList().size();
      return _total;
   }

   /**
    * Utility method for implementations to set the map that backs
    * this bag. Not intended for interactive use outside of
    * subclasses.
    **/
   protected void setMap(Map m) {
      _map = m;
   }

   /**
    * Utility method for implementations to access the map that backs
    * this bag. Not intended for interactive use outside of
    * subclasses.
    **/
   protected Map getMap() {
      return _map;
   }

   /**
    * Create a list for use in iteration, etc.
    **/
   private List extractList() {
      List result = new ArrayList();
      Iterator i = uniqueSet().iterator();
      while (i.hasNext()) {
         Object current = i.next();
         for (int index = getCount(current); index > 0; index--) {
            result.add(current);
         }
      }
      return result;
   }

   /**
    * Return number of modifications for iterator
    **/
   private int modCount() {
      return _mods;
   }

    /**
     *  Implement a toString() method suitable for debugging
     **/
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        Iterator i = uniqueSet().iterator();
        while(i.hasNext()) {
            Object current = i.next();
            int count = getCount(current);
            buf.append(count);
            buf.append(":");
            buf.append(current);
            if(i.hasNext()) {
                buf.append(",");
            }
        }
        buf.append("]");
        return buf.toString();
    }
}



