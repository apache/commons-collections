/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/LazyCollections.java,v 1.3 2002/06/16 03:39:40 mas Exp $
 * $Revision: 1.3 $
 * $Date: 2002/06/16 03:39:40 $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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

import java.util.*;
import java.lang.reflect.*;

/**
 * This is the factory manager for the lazy object creation collections. The
 * collections are within package scope only, so factory creation can be
 * standardised for these collections, and to provide a more convenient single
 * point entry for their use.
 *
 * Use the static factory methods to wrap your various collections in the Lazy
 * implementations so they can creat objects for incomming requestes.
 *
 * This class also holds the standard object factory, which has the means to
 * create simply the objects for the incomming requests. If all you need is a
 * new class built from an empty constructor, then all you need to provide is
 * the class definition.
 *
 * @author Arron Bates
 * @version $Revision: 1.3 $
 */
public class LazyCollections {


  /** Creates a LazyMap whith the provided object factory.
   *
   * @param inMap the java.util.Map implementation we have to wrap.
   * @param factory SimpleObjectFactory for new object creation
   * @return the wrapped Map reference
   */
  public static Map lazyMap(Map inMap, SimpleObjectFactory factory) {
    /* creates a new lazy map with the provided factory */
	  return new LazyMap(inMap, factory);
  }

  /** Creates a LazyMap whith the class definition, which will be used to create
   * a SimpleObjectFactory which will create a new object from an empty
   * constructor.
   *
   * @param inMap the java.util.Map implementation we have to wrap.
   * @param inClass class definition which will be ued to create the new object
   * @return the wrapped Map reference
   */
  public static Map lazyMap(Map inMap, Class inClass) {
    /* creates a new lazy map with a new object factory */
    SimpleObjectFactory f = FactoryUtils.createStandardFactory(inClass);
	  return new LazyMap(inMap, f);
  }

  /** Creates a LazyMap whith the class definition and argument details, which
   * will be used to create a SimpleObjectFactory which will create a new object
   * from a constructor which requires arguments.
   *
   * @param inMap the java.util.Map implementation we have to wrap.
   * @param inClass class definition which will be ued to create the new object
   * @param argTypes argument class types for the constructor
   * @param argObjects the objects for the arguments themselves
   * @return the wrapped Map reference
   */
  public static Map lazyMap(Map inMap, Class inClass, Class[] argTypes,
                            Object[] argObjects) {
    /* creates a new lazy map with a new object factory */
    SimpleObjectFactory f = FactoryUtils.createStandardFactory(inClass, argTypes, argObjects);
    return new LazyMap(inMap, f);
  }


  /** Creates a LazySortedMap whith the provided object factory.
   *
   * @param inMap the java.util.SortedMap implementation we have to wrap.
   * @param factory SimpleObjectFactory for new object creation
   * @return the wrapped SortedMap reference
   */
  public static SortedMap lazySortedMap(SortedMap inMap,
                                        SimpleObjectFactory factory) {
    /* creates a new lazy sorted map with the provided factory */
	  return new LazySortedMap(inMap, factory);
  }

  /** Creates a LazySortedMap whith the class definition, which will be used to
   * create a SimpleObjectFactory which will create a new object from an empty
   * constructor.
   *
   * @param inMap the java.util.SortedMap implementation we have to wrap.
   * @param inClass class definition which will be ued to create the new object
   * @return the wrapped SortedMap reference
   */
  public static SortedMap lazySortedMap(SortedMap inMap, Class inClass) {
    /* creates a new lazy sorted map with a new object factory */
    SimpleObjectFactory f = FactoryUtils.createStandardFactory(inClass);
	  return new LazySortedMap(inMap, f);
  }

  /** Creates a LazySortedMap whith the class definition and argument details,
   * which will be used to create a SimpleObjectFactory which will create a new
   * object from a constructor which requires arguments.
   *
   * @param inMap the java.util.SortedMap implementation we have to wrap.
   * @param inClass class definition which will be ued to create the new object
   * @param argTypes argument class types for the constructor
   * @param argObjects the objects for the arguments themselves
   * @return the wrapped SortedMap reference
   */
  public static SortedMap lazySortedMap(SortedMap inMap, Class inClass,
                                        Class[] argTypes, Object[] argObjects) {
    /* creates a new lazy sorted map with a new object factory */
    SimpleObjectFactory f = FactoryUtils.createStandardFactory(inClass, argTypes, argObjects);
    return new LazySortedMap(inMap, f);
  }


  /** Creates a LazyList whith the provided object factory.
   *
   * @param inMap the java.util.List implementation we have to wrap.
   * @param factory SimpleObjectFactory for new object creation
   * @return the wrapped List reference
   */
  public static List lazyList(List inList, SimpleObjectFactory factory) {
    /* creates a new lazy list with the provided factory */
    return new LazyList(inList, factory);
  }

  /** Creates a LazyList whith the class definition, which will be used to
   * create a SimpleObjectFactory which will create a new object from an empty
   * constructor.
   *
   * @param inMap the java.util.List implementation we have to wrap.
   * @param inClass class definition which will be ued to create the new object
   * @return the wrapped List reference
   */
  public static List lazyList(List inList, Class inClass) {
    /* creates a new lazy list with a new object factory */
    SimpleObjectFactory f = FactoryUtils.createStandardFactory(inClass);
    return new LazyList(inList, f);
  }

  /** Creates a LazyList whith the class definition and argument details,
   * which will be used to create a SimpleObjectFactory which will create a new
   * object from a constructor which requires arguments.
   *
   * @param inMap the java.util.List implementation we have to wrap.
   * @param inClass class definition which will be ued to create the new object
   * @param argTypes argument class types for the constructor
   * @param argObjects the objects for the arguments themselves
   * @return the wrapped List reference
   */
  public static List lazyList(List inList, Class inClass, Class[] argTypes,
                              Object[] argObjects) {
    /* creates a new lazy list with a new object factory */
    SimpleObjectFactory f = FactoryUtils.createStandardFactory(inClass, argTypes, argObjects);
    return new LazyList(inList, f);
  }



  /** Cleans a List implementation from nulls. Because a rampant index up the
   * line can create many nulls. At some point, the collection has to become
   * useful outside of reliable index use. clean() does this.
   *
   * @param inList Lsit to rip the nulls out of
   */
  public static void clean(List inList) {
    /* loop through backwards, removing out any nulls found */
    for (int i = (inList.size()-1); i >= 0; i--) {
      if (inList.get(i) == null) {
        inList.remove(i);
      }
    }
  }



  /* This is a java.util.List implementation which provides the means of objects
   * when requested. When a system expects an object to be provided when accessed
   * via an index, this collection has been provided the rules (factory reference)
   * to create an object, add it to the list and return it to the request.
   *
   * For example, when a request comes into the Struts controller for a bean, it
   * will created the bean. These request references can be indexed or mapped.
   * Problem for many reasons is that there is not an object waiting within the
   * session object. To recieve these updates, objects have to be created from the
   * collections that hold them.
   *
   * Only issue for lists, is that indexes will most often ben requested outside
   * the boulds of the list. This implementation will pack the spaces with null
   * objects. To make the list useful to business logic, a call to
   * <code>clean()</code> will clear the list of these null references.
   */
  private static class LazyList implements List {


    /* Builds a LazyList with the provided SimpleObjectFactory as the means of
     * creating the objects.
     */
    public LazyList(List inList, SimpleObjectFactory factory) {
      this.listImpl = inList;
      this.factory = factory;
    }


    /* Proxy method to the impl's get method. With the exception that if it's out
     * of bounds, then the collection will grow, leaving place-holders in its
     * wake, so that an item can be set at any given index. Later the
     * place-holders are removed to return to a pure collection.
     *
     * If there's a place-holder at the index, then it's replaced with a proper
     * object to be used.
     */
    public Object get(int index) {
      Object obj;
      if (index < (this.listImpl.size()-1)) {
        /* within bounds, get the object */
        obj = this.listImpl.get(index);
        if (obj == null) {
          /* item is a place holder, create new one, set and return */
          obj = this.factory.createObject();
          this.listImpl.set(index, obj);
          return obj;
        } else {
          /* good and ready to go */
          return obj;
        }
      } else {
        /* we have to grow the list */
        for (int i = this.listImpl.size(); i < index; i++) {
          this.listImpl.add(null);
        }
        /* create our last object, set and return */
        obj = this.factory.createObject();
        this.listImpl.add(obj);
        return obj;
      }
    }


    /* proxy the call to the provided list implementation. */
    public List subList(int fromIndex, int toIndex) {
      /* wrap the returned sublist so it can continue the functionality */
      return new LazyList(this.listImpl.subList(fromIndex, toIndex), factory);
    }

    /* proxy the call to the provided list implementation.*/
    public int size() {
      return this.listImpl.size();
    }

    /* proxy the call to the provided list implementation. */
    public boolean isEmpty() {
      return this.listImpl.isEmpty();
    }

    /* proxy the call to the provided list implementation. */
    public boolean contains(Object o) {
      return this.listImpl.contains(o);
    }

    /* proxy the call to the provided list implementation. */
    public Iterator iterator() {
      return this.listImpl.iterator();
    }

    /* proxy the call to the provided list implementation. */
    public Object[] toArray() {
      return this.listImpl.toArray();
    }

    /* proxy the call to the provided list implementation. */
    public Object[] toArray(Object[] a) {
      return this.listImpl.toArray(a);
    }

    /* proxy the call to the provided list implementation. */
    public boolean add(Object o) {
      return this.listImpl.add(o);
    }

    /* proxy the call to the provided list implementation. */
    public boolean remove(Object o) {
      return this.listImpl.remove(o);
    }

    /* proxy the call to the provided list implementation. */
    public boolean containsAll(Collection c) {
      return this.listImpl.containsAll(c);
    }

    /* proxy the call to the provided list implementation. */
    public boolean addAll(Collection c) {
      return this.listImpl.addAll(c);
    }

    /* proxy the call to the provided list implementation. */
    public boolean addAll(int index, Collection c) {
      return this.listImpl.addAll(index, c);
    }

    /* proxy the call to the provided list implementation. */
    public boolean removeAll(Collection c) {
      return this.listImpl.removeAll(c);
    }

    /* proxy the call to the provided list implementation. */
    public boolean retainAll(Collection c) {
      return this.listImpl.retainAll(c);
    }

    /* proxy the call to the provided list implementation. */
    public void clear() {
      this.listImpl.clear();
    }

    /* proxy the call to the provided list implementation. */
    public Object set(int index, Object element) {
      return this.listImpl.set(index, element);
    }

    /* proxy the call to the provided list implementation. */
    public void add(int index, Object element) {
      this.listImpl.add(index, element);
    }

    /* proxy the call to the provided list implementation. */
    public Object remove(int index) {
      return this.listImpl.remove(index);
    }

    /* proxy the call to the provided list implementation. */
    public int indexOf(Object o) {
      return this.listImpl.indexOf(o);
    }

    /* proxy the call to the provided list implementation. */
    public int lastIndexOf(Object o) {
      return this.listImpl.lastIndexOf(o);
    }

    /* proxy the call to the provided list implementation. */
    public ListIterator listIterator() {
      return this.listImpl.listIterator();
    }

    /* proxy the call to the provided list implementation. */
    public ListIterator listIterator(int index) {
      return this.listImpl.listIterator(index);
    }



    /* java.util.List implementation to proxy against */
    private List listImpl;

    /* optional object factory */
    private SimpleObjectFactory factory;
  }




  /* This is a java.util.Map implementation which provides the means of objects
   * when requested. When a system expects an object to be provided when accessed
   * via a key, this collection has been provided the rules (factory reference)
   * to create an object, add it to the map and return it to the request.
   *
   * For example, when a request comes into the Struts controller for a bean, it
   * will created the bean. These request references can be indexed or mapped.
   * Problem for many reasons is that there is not an object waiting within the
   * session object. To recieve these updates, objects have to be created from the
   * collections that hold them.
   */
  private static class LazyMap implements Map {


    /* Builds a LazyMap with the provided SimpleObjectFactory as the means of
     * creating the objects.
     */
    public LazyMap(Map inMap, SimpleObjectFactory factory) {
      this.mapImpl = inMap;
      this.factory = factory;
    }


    /* Proxy method to the impl's get method. With the exception that if there
     * is no keyed object waiting for it, an object will be created, set and
     * returned.
     */
    public Object get(Object key) {
      Object obj = this.mapImpl.get(key);
      if (obj == null) {
        /* create our last object, set and return */
        obj = this.factory.createObject();
        this.mapImpl.put(key, obj);
      }
      return obj;
    }


    /* proxy the call to the provided Map implementation. */
    public int size() {
      return this.mapImpl.size();
    }

    /* proxy the call to the provided Map implementation. */
    public boolean isEmpty() {
      return this.mapImpl.isEmpty();
    }

    /* proxy the call to the provided Map implementation. */
    public boolean containsKey(Object key) {
      return this.mapImpl.containsKey(key);
    }

    /* proxy the call to the provided Map implementation. */
    public boolean containsValue(Object value) {
      return this.mapImpl.containsValue(value);
    }

    /* proxy the call to the provided Map implementation. */
    public Object put(Object key, Object value) {
      return this.mapImpl.put(key, value);
    }

    /* proxy the call to the provided Map implementation. */
    public Object remove(Object key) {
      return this.mapImpl.remove(key);
    }

    /* proxy the call to the provided Map implementation. */
    public void putAll(Map t) {
      this.mapImpl.putAll(t);
    }

    /* proxy the call to the provided Map implementation. */
    public void clear() {
      this.mapImpl.clear();
    }

    /* proxy the call to the provided Map implementation. */
    public Set keySet() {
      return this.mapImpl.keySet();
    }

    /* proxy the call to the provided Map implementation. */
    public Collection values() {
      return this.mapImpl.values();
    }

    /* proxy the call to the provided Map implementation. */
    public Set entrySet() {
      return this.mapImpl.entrySet();
    }

    /* proxy the call to the provided Map implementation. */
    public boolean equals(Object o) {
      return this.mapImpl.equals(o);
    }

    /* proxy the call to the provided Map implementation. */
    public int hashCode() {
      return this.mapImpl.hashCode();
    }


    /* java.util.Map implementation to proxy against */
    protected Map mapImpl;

    /* optional object factory */
    protected SimpleObjectFactory factory;
  }





  /* This is a java.util.SortedMap implementation which provides the means of objects
   * when requested. When a system expects an object to be provided when accessed
   * via a key, this collection has been provided the rules (factory reference)
   * to create an object, add it to the map and return it to the request.
   *
   * For example, when a request comes into the Struts controller for a bean, it
   * will created the bean. These request references can be indexed or mapped.
   * Problem for many reasons is that there is not an object waiting within the
   * session object. To recieve these updates, objects have to be created from the
   * collections that hold them.
   */
  private static class LazySortedMap extends LazyMap implements SortedMap {


    /* Builds a LazySortedMap with the provided SimpleObjectFactory as the means of
     * creating the objects.
     */
    public LazySortedMap(SortedMap inMap, SimpleObjectFactory factory) {
      super(inMap, factory);
    }


    /* proxy the call to the provided LazySortedMap implementation. */
    public Comparator comparator() {
      return ((SortedMap)super.mapImpl).comparator();
    }

    /* proxy the call to the provided LazySortedMap implementation. */
    public SortedMap subMap(Object fromKey, Object toKey) {
      SortedMap subby = ((SortedMap)super.mapImpl).subMap(fromKey, toKey);
      return new LazySortedMap(subby, super.factory);
    }

    /* proxy the call to the provided LazySortedMap implementation. */
    public SortedMap headMap(Object toKey) {
      SortedMap heady = ((SortedMap)super.mapImpl).headMap(toKey);
      return new LazySortedMap(heady, super.factory);
    }

    /* proxy the call to the provided LazySortedMap implementation. */
    public SortedMap tailMap(Object fromKey) {
      SortedMap tailer = ((SortedMap)super.mapImpl).tailMap(fromKey);
      return new LazySortedMap(tailer, super.factory);
    }

    /* proxy the call to the provided LazySortedMap implementation. */
    public Object firstKey() {
      return ((SortedMap)super.mapImpl).firstKey();
    }

    /* proxy the call to the provided LazySortedMap implementation. */
    public Object lastKey() {
      return ((SortedMap)super.mapImpl).lastKey();
    }
  }
}
