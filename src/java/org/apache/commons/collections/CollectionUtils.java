/*
 * $Id: CollectionUtils.java,v 1.20 2002/11/24 16:23:21 scolebourne Exp $
 * $Revision: 1.20 $
 * $Date: 2002/11/24 16:23:21 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.EnumerationIterator;
/**
 * A set of {@link Collection} related utility methods.
 *
 * @since 1.0
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Steve Downey
 * @author <a href="herve.quiroz@esil.univ-mrs.fr">Herve Quiroz</a>
 * @version $Revision: 1.20 $ $Date: 2002/11/24 16:23:21 $
 */
public class CollectionUtils {

    /**
     * The empty iterator (immutable).
     * @deprecated use IteratorUtils.EMPTY_ITERATOR
     */
    public static final Iterator EMPTY_ITERATOR = IteratorUtils.EMPTY_ITERATOR;

    /**
     * Please don't ever instantiate a <code>CollectionUtils</code>.
     */
    public CollectionUtils() {
    }

    /**
     * Returns a {@link Collection} containing the union
     * of the given {@link Collection}s.
     * <p>
     * The cardinality of each element in the returned {@link Collection}
     * will be equal to the maximum of the cardinality of that element
     * in the two given {@link Collection}s.
     *
     * @see Collection#addAll
     */
    public static Collection union(final Collection a, final Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        Set elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();
        while(it.hasNext()) {
            Object obj = it.next();
            for(int i=0,m=Math.max(getFreq(obj,mapa),getFreq(obj,mapb));i<m;i++) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * Returns a {@link Collection} containing the intersection
     * of the given {@link Collection}s.
     * <p>
     * The cardinality of each element in the returned {@link Collection}
     * will be equal to the minimum of the cardinality of that element
     * in the two given {@link Collection}s.
     *
     * @see Collection#retainAll
     * @see #containsAny
     */
    public static Collection intersection(final Collection a, final Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        Set elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();
        while(it.hasNext()) {
            Object obj = it.next();
            for(int i=0,m=Math.min(getFreq(obj,mapa),getFreq(obj,mapb));i<m;i++) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * Returns a {@link Collection} containing the exclusive disjunction
     * (symmetric difference) of the given {@link Collection}s.
     * <p>
     * The cardinality of each element <i>e</i> in the returned {@link Collection}
     * will be equal to
     * <tt>max(cardinality(<i>e</i>,<i>a</i>),cardinality(<i>e</i>,<i>b</i>)) - min(cardinality(<i>e</i>,<i>a</i>),cardinality(<i>e</i>,<i>b</i>))</tt>.
     * <p>
     * This is equivalent to
     * <tt>{@link #subtract subtract}({@link #union union(a,b)},{@link #intersection intersection(a,b)})</tt>
     * or
     * <tt>{@link #union union}({@link #subtract subtract(a,b)},{@link #subtract subtract(b,a)})</tt>.
     */
    public static Collection disjunction(final Collection a, final Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        Set elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();
        while(it.hasNext()) {
            Object obj = it.next();
            for(int i=0,m=((Math.max(getFreq(obj,mapa),getFreq(obj,mapb)))-(Math.min(getFreq(obj,mapa),getFreq(obj,mapb))));i<m;i++) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * Returns a {@link Collection} containing <tt><i>a</i> - <i>b</i></tt>.
     * The cardinality of each element <i>e</i> in the returned {@link Collection}
     * will be the cardinality of <i>e</i> in <i>a</i> minus the cardinality
     * of <i>e</i> in <i>b</i>, or zero, whichever is greater.
     *
     * @see Collection#removeAll
     */
    public static Collection subtract(final Collection a, final Collection b) {
        ArrayList list = new ArrayList( a );
        Iterator it =  b.iterator();
        while(it.hasNext()) {
            list.remove(it.next());
        }
        return list;
    }

    /**
     * Returns <code>true</code> iff some element of <i>a</i>
     * is also an element of <i>b</i> (or, equivalently, if 
     * some element of <i>b</i> is also an element of <i>a</i>).
     * In other words, this method returns <code>true</code>
     * iff the {@link #intersection} of <i>a</i> and <i>b</i>
     * is not empty.
     * @since 2.1
     * @param a a non-<code>null</code> Collection
     * @param b a non-<code>null</code> Collection
     * @return <code>true</code> iff the intersection of <i>a</i> and <i>b</i> is non-empty
     * @see #intersection
     */
    public static boolean containsAny(final Collection a, final Collection b) {
        // TO DO: we may be able to optimize this by ensuring either a or b
        // is the larger of the two Collections, but I'm not sure which.
        for(Iterator iter = a.iterator(); iter.hasNext();) {
            if(b.contains(iter.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a {@link Map} mapping each unique element in
     * the given {@link Collection} to an {@link Integer}
     * representing the number of occurances of that element
     * in the {@link Collection}.
     * An entry that maps to <tt>null</tt> indicates that the
     * element does not appear in the given {@link Collection}.
     */
    public static Map getCardinalityMap(final Collection col) {
        HashMap count = new HashMap();
        Iterator it = col.iterator();
        while(it.hasNext()) {
            Object obj = it.next();
            Integer c = (Integer)(count.get(obj));
            if(null == c) {
                count.put(obj,new Integer(1));
            } else {
                count.put(obj,new Integer(c.intValue() + 1));
            }
        }
        return count;
    }

    /**
     * Returns <tt>true</tt> iff <i>a</i> is a sub-collection of <i>b</i>,
     * that is, iff the cardinality of <i>e</i> in <i>a</i> is less
     * than or equal to the cardinality of <i>e</i> in <i>b</i>,
     * for each element <i>e</i> in <i>a</i>.
     *
     * @see #isProperSubCollection
     * @see Collection#containsAll
     */
    public static boolean isSubCollection(final Collection a, final Collection b) {
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        Iterator it = a.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (getFreq(obj, mapa) > getFreq(obj, mapb)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> iff <i>a</i> is a <i>proper</i> sub-collection of <i>b</i>,
     * that is, iff the cardinality of <i>e</i> in <i>a</i> is less
     * than or equal to the cardinality of <i>e</i> in <i>b</i>,
     * for each element <i>e</i> in <i>a</i>, and there is at least one
     * element <i>f</i> such that the cardinality of <i>f</i> in <i>b</i>
     * is strictly greater than the cardinality of <i>f</i> in <i>a</i>.
     *
     * @see #isSubCollection
     * @see Collection#containsAll
     */
    public static boolean isProperSubCollection(final Collection a, final Collection b) {
        // XXX optimize me!
        return CollectionUtils.isSubCollection(a,b) && (!(CollectionUtils.isEqualCollection(a,b)));
    }

    /**
     * Returns <tt>true</tt> iff the given {@link Collection}s contain
     * exactly the same elements with exactly the same cardinality.
     * <p>
     * That is, iff the cardinality of <i>e</i> in <i>a</i> is
     * equal to the cardinality of <i>e</i> in <i>b</i>,
     * for each element <i>e</i> in <i>a</i> or <i>b</i>.
     */
    public static boolean isEqualCollection(final Collection a, final Collection b) {
        if(a.size() != b.size()) {
            return false;
        } else {
            Map mapa = getCardinalityMap(a);
            Map mapb = getCardinalityMap(b);
            if(mapa.size() != mapb.size()) {
                return false;
            } else {
                Iterator it = mapa.keySet().iterator();
                while(it.hasNext()) {
                    Object obj = it.next();
                    if(getFreq(obj,mapa) != getFreq(obj,mapb)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    /**
     * Returns the number of occurrences of <i>obj</i>
     * in <i>col</i>.
     */
    public static int cardinality(Object obj, final Collection col) {
        int count = 0;
        if(null == obj) {
            for(Iterator it = col.iterator();it.hasNext();) {
                if(null == it.next()) {
                    count++;
                }
            }
        } else {
            for(Iterator it = col.iterator();it.hasNext();) {
                if(obj.equals(it.next())) {
                    count++;
                }
            }
        }
        return count;
    }

    /** 
     * Finds the first element in the given collection which matches the given predicate.
     * <p>
     * If the input collection or predicate is null, null is returned.
     *
     * @param collection  the collection to search, may be null
     * @param predicate  the predicate to use, may be null
     * @return the first element of the collection which matches the predicate or null if none could be found
     */
    public static Object find(Collection collection, Predicate predicate) {
        if (collection != null && predicate != null) {
            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                Object item = iter.next();
                if (predicate.evaluate(item)) {
                    return item;
                }
            }
        }
        return null;
    }
    
    /** 
     * Executes the given closure on each element in the collection.
     * <p>
     * If the input collection is null, there is no change made.
     * 
     * @param collection  the collection to get the input from, may be null
     * @param closure  the closure to perform, may not be null
     * @throws NullPointerException if the closure is null
     */
    public static void forAllDo(Collection collection, Closure closure) {
        if (collection != null) {
            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                Object element = iter.next();
                closure.execute(element);
            }
        }
    }

    /** 
     * Filter the collection by applying a Predicate to each element. If the
     * predicate returns false, remove the element.
     * <p>
     * If the input collection or predicate is null, there is no change made.
     * 
     * @param collection  the collection to get the input from, may be null
     * @param predicate  the predicate to use as a filter, may be null
     */
    public static void filter(Collection collection, Predicate predicate) {
        if (collection != null && predicate != null) {
            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                Object element = iter.next();
                if (predicate.evaluate(element) == false) {
                    iter.remove();
                }
            }
        }
    }

    /** 
     * Transform the collection by applying a Transformer to each element.
     * <p>
     * If the input collection or transformer is null, there is no change made.
     * <p>
     * This routine is best for Lists and uses set(), however it adapts for all
     * Collections that support clear() and addAll().
     * <p>
     * If the input collection controls its input, such as a Set, and the
     * Transformer creates duplicates (or are otherwise invalid), the 
     * collection may reduce in size due to calling this method.
     * 
     * @param collection  the collection to get the input from, may be null
     * @param transformer  the transformer to perform, may be null
     */
    public static void transform(Collection collection, Transformer transformer) {
        if (collection != null && transformer != null) {
            if (collection instanceof List) {
                List list = (List) collection;
                for (ListIterator iter = list.listIterator(); iter.hasNext();) {
                    Object element = iter.next();
                    iter.set(transformer.transform(element));
                }
            } else {
                Collection resultCollection = collect(collection, transformer);
                collection.clear();
                collection.addAll(resultCollection);
            }
        }
    }

    /** 
     * Selects all elements from input collection which match the given predicate
     * into an output collection.
     * 
     * @param inputCollection  the collection to get the input from, may not be null
     * @param predicate  the predicate to use, may be null
     * @return the elements matching the predicate (new list)
     * @throws NullPointerException if the input collection is null
     */
    public static Collection select(Collection inputCollection, Predicate predicate) {
        ArrayList answer = new ArrayList(inputCollection.size());
        select(inputCollection, predicate, answer);
        return answer;
    }

    /** 
     * Selects all elements from input collection which match the given predicate
     * and adds them to outputCollection.
     * <p>
     * If the input collection or predicate is null, there is no change to the 
     * output collection.
     * 
     * @param inputCollection  the collection to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @param outputCollection  the collection to output into, may not be null
     * @return the outputCollection with the the elements matching the predicate added
     * @throws NullPointerException if the input collection is null
     */
    public static void select(Collection inputCollection, Predicate predicate, Collection outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (Iterator iter = inputCollection.iterator(); iter.hasNext();) {
                Object item = iter.next();
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
    }
    
    /** 
     * Transforms all elements from inputCollection with the given transformer 
     * and adds them to the outputCollection.
     * <p>
     * If the input transfomer is null, the result is an empty list.
     * 
     * @param inputCollection  the collection to get the input from, may not be null
     * @param transformer  the transformer to use, may be null
     * @return the transformed result (new list)
     * @throws NullPointerException if the input collection is null
     */
    public static Collection collect(Collection inputCollection, Transformer transformer) {
        ArrayList answer = new ArrayList(inputCollection.size());
        collect(inputCollection, transformer, answer);
        return answer;
    }
    
    /** 
     * Transforms all elements from the inputIterator  with the given transformer 
     * and adds them to the outputCollection.
     * <p>
     * If the input iterator or transfomer is null, the result is an empty list.
     * 
     * @param inputIterator  the iterator to get the input from, may be null
     * @param transformer  the transformer to use, may be null
     * @return the transformed result (new list)
     */
    public static Collection collect(Iterator inputIterator, Transformer transformer) {
        ArrayList answer = new ArrayList();
        collect(inputIterator, transformer, answer);
        return answer;
    }
    
    /** 
     * Transforms all elements from inputCollection with the given transformer 
     * and adds them to the outputCollection.
     * <p>
     * If the input collection or transfomer is null, there is no change to the 
     * output collection.
     *
     * @param inputCollection  the collection to get the input from, may be null
     * @param transformer  the transformer to use, may be null
     * @param outputCollection  the collection to output into, may not be null
     * @return the outputCollection with the transformed input added
     * @throws NullPointerException if the output collection is null
     */
    public static Collection collect(Collection inputCollection, final Transformer transformer, final Collection outputCollection) {
        if (inputCollection != null) {
            return collect(inputCollection.iterator(), transformer, outputCollection);
        }
        return outputCollection;
    }

    /** 
     * Transforms all elements from the inputIterator with the given transformer 
     * and adds them to the outputCollection.
     * <p>
     * If the input iterator or transfomer is null, there is no change to the 
     * output collection.
     *
     * @param inputIterator  the iterator to get the input from, may be null
     * @param transformer  the transformer to use, may be null
     * @param outputCollection  the collection to output into, may not be null
     * @return the outputCollection with the transformed input added
     * @throws NullPointerException if the output collection is null
     */
    public static Collection collect(Iterator inputIterator, final Transformer transformer, final Collection outputCollection) {
        if (inputIterator != null && transformer != null) {
            while (inputIterator.hasNext()) {
                Object item = inputIterator.next();
                Object value = transformer.transform(item);
                outputCollection.add(value);
            }
        }
        return outputCollection;
    }

    /**
     * Adds all elements in the iteration to the given collection.
     * 
     * @param collection  the collection to add to
     * @param iterator  the iterator of elements to add, may not be null
     * @throws NullPointerException if the collection or iterator is null
     */
    public static void addAll(Collection collection, Iterator iterator) {
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
    }
    
    /**
     * Adds all elements in the enumeration to the given collection.
     * 
     * @param collection  the collection to add to
     * @param enumeration  the enumeration of elements to add, may not be null
     * @throws NullPointerException if the collection or enumeration is null
     */
    public static void addAll(Collection collection, Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            collection.add(enumeration.nextElement());
        }
    }    
    
    /** 
     * Adds all elements in the array to the given collection.
     * 
     * @param collection  the collection to add to
     * @param elements  the array of elements to add, may be null
     * @throws NullPointerException if the collection or array is null
     */
    public static void addAll(Collection collection, Object[] elements) {
        for (int i = 0, size = elements.length; i < size; i++) {
            collection.add(elements[i]);
        }
    }    
    
    /**
     * Given an Object, and an index, it will get the nth value in the
     * object.
     * <ul>
     * <li>If obj is a Map, get the nth value from the <b>key</b> iterator.
     * <li>If obj is a List or an array, get the nth value.
     * <li>If obj is an iterator, enumeration or Collection, get the nth value from the iterator.
     * <li>Return the original obj.
     * </ul>
     * 
     * @param obj  the object to get an index of
     * @param index  the index to get
     * @throws IndexOutOfBoundsException
     * @throws NoSuchElementException
     */
    public static Object index(Object obj, int idx) {
        return index(obj, new Integer(idx));
    }
    
    /**
     * Given an Object, and a key (index), it will get value associated with
     * that key in the Object. The following checks are made:
     * <ul>
     * <li>If obj is a Map, use the index as a key to get a value. If no match continue.
     * <li>Check key is an Integer. If not, return the object passed in.
     * <li>If obj is a Map, get the nth value from the <b>key</b> iterator.
     * <li>If obj is a List or an array, get the nth value.
     * <li>If obj is an iterator, enumeration or Collection, get the nth value from the iterator.
     * <li>Return the original obj.
     * </ul>
     * 
     * @param obj  the object to get an index of
     * @param index  the index to get
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException
     * @throws NoSuchElementException
     */
    public static Object index(Object obj, Object index) {
        if(obj instanceof Map) {
            Map map = (Map)obj;
            if(map.containsKey(index)) {
                return map.get(index);
            }
        }
        int idx = -1;
        if(index instanceof Integer) {
            idx = ((Integer)index).intValue();
        }
        if(idx < 0) {
            return obj;
        } 
        else if(obj instanceof Map) {
            Map map = (Map)obj;
            Iterator iterator = map.keySet().iterator();
            return index(iterator, idx);
        } 
        else if(obj instanceof List) {
            return ((List)obj).get(idx);
        } 
        else if(obj instanceof Object[]) {
            return ((Object[])obj)[idx];
        } 
        else if(obj instanceof Enumeration) {
            Enumeration enum = (Enumeration)obj;
            while(enum.hasMoreElements()) {
                idx--;
                if(idx == -1) {
                    return enum.nextElement();
                } else {
                    enum.nextElement();
                }
            }
        } 
        else if(obj instanceof Iterator) {
            return index((Iterator)obj, idx);
        }
        else if(obj instanceof Collection) {
            Iterator iterator = ((Collection)obj).iterator();
            return index(iterator, idx);
        }
        return obj;
    }

    private static Object index(Iterator iterator, int idx) {
        while(iterator.hasNext()) {
            idx--;
            if(idx == -1) {
                return iterator.next();
            } else {
                iterator.next();
            }
        }
        return iterator;
    }

    /** 
     * Returns an Iterator for the given object. Currently this method can handle
     * Iterator, Enumeration, Collection, Map, Object[] or array.
     * 
     * @deprecated use IteratorUtils version instead
     */
    public static Iterator getIterator(Object obj) {
        if(obj instanceof Iterator) {
            return (Iterator)obj;
        } 
        else if(obj instanceof Collection) {
            return ((Collection)obj).iterator();
        } 
        else if(obj instanceof Object[]) {
            return new ArrayIterator( obj );
        } 
        else if(obj instanceof Enumeration) {
            return new EnumerationIterator( (Enumeration)obj );
        } 
        else if(obj instanceof Map) {
            return ((Map)obj).values().iterator();
        } 
        else if(obj != null && obj.getClass().isArray()) {
            return new ArrayIterator( obj );
        }
        else{
            return null;
        }
    }


    /** 
     * Reverses the order of the given array 
     * 
     * @param array  the array to reverse
     */
    public static void reverseArray(Object[] array) {
        int i = 0;
        int j = array.length - 1;
        Object tmp;

        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    private static final int getFreq(final Object obj, final Map freqMap) {
        try {
            return ((Integer)(freqMap.get(obj))).intValue();
        } catch(NullPointerException e) {
            // ignored
        } catch(NoSuchElementException e) {
            // ignored
        }
        return 0;
    }

    /**
     * Returns true if no more elements can be added to the Collection.
     * <p>
     * This method uses the {@link BoundedCollection} class to determine the
     * full status. If the collection does not implement this interface then
     * false is returned.
     * <p>
     * This method handles the synchronized, blocking, unmodifiable 
     * and predicated decorators.
     *
     * @return  true if the Collection is full
     * @throws NullPointerException if the collection is null
     */
    public static boolean isFull(Collection coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        Collection unwrappedCollection = coll;
        
        // handle decorators
        while (true) {
            if (unwrappedCollection instanceof CollectionUtils.CollectionWrapper) {
                unwrappedCollection = ((CollectionUtils.CollectionWrapper) unwrappedCollection).collection;
            } else if (unwrappedCollection instanceof CollectionUtils.SynchronizedCollection) {
                unwrappedCollection = ((CollectionUtils.SynchronizedCollection) unwrappedCollection).collection;
            } else {
                break;
            }
        }
        
        // is it full
        if (unwrappedCollection instanceof BoundedCollection) {
            return ((BoundedCollection) unwrappedCollection).isFull();
        }
        return false;
    }

    /**
     * Get the maximum number of elements that the Collection can contain.
     * <p>
     * This method uses the {@link BoundedCollection} class to determine the
     * maximum size. If the collection does not implement this interface then
     * -1 is returned.
     * <p>
     * This method handles the synchronized, blocking, unmodifiable 
     * and predicated decorators.
     *
     * @return the maximum size of the Collection, -1 if no maximum size
     * @throws NullPointerException if the collection is null
     */
    public static int maxSize(Collection coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        Collection unwrappedCollection = coll;
        
        // handle decorators
        while (true) {
            if (unwrappedCollection instanceof CollectionUtils.CollectionWrapper) {
                unwrappedCollection = ((CollectionUtils.CollectionWrapper) unwrappedCollection).collection;
            } else if (unwrappedCollection instanceof CollectionUtils.SynchronizedCollection) {
                unwrappedCollection = ((CollectionUtils.SynchronizedCollection) unwrappedCollection).collection;
            } else {
                break;
            }
        }
        
        // get max size
        if (unwrappedCollection instanceof BoundedCollection) {
            return ((BoundedCollection) unwrappedCollection).maxSize();
        }
        return -1;
    }

    /**
     * Base class for collection decorators.  I decided to do it this way
     * because it seemed to result in the most reuse.  
     * 
     * Inner class tree looks like:
     * <pre>
     *       CollectionWrapper
     *          PredicatedCollection
     *             PredicatedSet
     *             PredicatedList
     *             PredicatedBag
     *             PredicatedBuffer
     *          UnmodifiableCollection
     *             UnmodifiableBag
     *             UnmodifiableBuffer
     *          LazyCollection
     *             LazyList
     *             LazyBag
     *       SynchronizedCollection
     *          SynchronizedBuffer
     *          SynchronizedBag
     *          SynchronizedBuffer
     * </pre>
     */
    static class CollectionWrapper 
            implements Collection {

        protected final Collection collection;

        public CollectionWrapper(Collection collection) {
            if (collection == null) {
                throw new IllegalArgumentException("Collection must not be null");
            }
            this.collection = collection;
        }

        public int size() {
            return collection.size();
        }

        public boolean isEmpty() {
            return collection.isEmpty();
        }

        public boolean contains(Object o) {
            return collection.contains(o);
        }

        public Iterator iterator() {
            return collection.iterator();
        }

        public Object[] toArray() {
            return collection.toArray();
        }

        public Object[] toArray(Object[] o) {
            return collection.toArray(o);
        }

        public boolean add(Object o) {
            return collection.add(o);
        }

        public boolean remove(Object o) {
            return collection.remove(o);
        }

        public boolean containsAll(Collection c2) {
            return collection.containsAll(c2);
        }

        public boolean addAll(Collection c2) {
            return collection.addAll(c2);
        }

        public boolean removeAll(Collection c2) {
            return collection.removeAll(c2);
        }

        public boolean retainAll(Collection c2) {
            return collection.retainAll(c2);
        }

        public void clear() {
            collection.clear();
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            return collection.equals(o);
        }

        public int hashCode() {
            return collection.hashCode();
        }

        public String toString() {
            return collection.toString();
        }

    }


    static class PredicatedCollection 
            extends CollectionWrapper {

        protected final Predicate predicate;

        public PredicatedCollection(Collection c, Predicate p) {
            super(c);
            if (p == null) {
                throw new IllegalArgumentException("Predicate must not be null");
            }
            this.predicate = p;
            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                validate(iter.next());
            }
        }

        public boolean add(Object o) {
            validate(o);
            return collection.add(o);
        }

        public boolean addAll(Collection c2) {
            for (Iterator iter = c2.iterator(); iter.hasNext(); ) {
                validate(iter.next());
            }
            return collection.addAll(c2);
        }

        protected void validate(Object o) {
            if (!predicate.evaluate(o)) {
                throw new IllegalArgumentException("Cannot add Object - Predicate rejected it");
            }
        }

    }


    static class UnmodifiableCollection 
            extends CollectionWrapper {

        public UnmodifiableCollection(Collection c) {
            super(c);
        }

        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public Iterator iterator() {
            return new UnmodifiableIterator(collection.iterator());
        }

    }


    static class SynchronizedCollection {

        protected final Collection collection;

        public SynchronizedCollection(Collection collection) {
            if (collection == null) {
                throw new IllegalArgumentException("Collection must not be null");
            }
            this.collection = collection;
        }

        public synchronized int size() {
            return collection.size();
        }

        public synchronized boolean isEmpty() {
            return collection.isEmpty();
        }

        public synchronized boolean contains(Object o) {
            return collection.contains(o);
        }

        public Iterator iterator() {
            return collection.iterator();
        }

        public synchronized Object[] toArray() {
            return collection.toArray();
        }

        public synchronized Object[] toArray(Object[] o) {
            return collection.toArray(o);
        }

        public synchronized boolean add(Object o) {
            return collection.add(o);
        }

        public synchronized boolean remove(Object o) {
            return collection.remove(o);
        }

        public synchronized boolean containsAll(Collection c2) {
            return collection.containsAll(c2);
        }

        public synchronized boolean addAll(Collection c2) {
            return collection.addAll(c2);
        }

        public synchronized boolean removeAll(Collection c2) {
            return collection.removeAll(c2);
        }

        public synchronized boolean retainAll(Collection c2) {
            return collection.retainAll(c2);
        }

        public synchronized void clear() {
            collection.clear();
        }

        public synchronized boolean equals(Object o) {
            return collection.equals(o);
        }

        public synchronized int hashCode() {
            return collection.hashCode();
        }

        public synchronized String toString() {
            return collection.toString();
        }

    }


    static class UnmodifiableIterator 
            implements Iterator {

        protected final Iterator iterator;

        public UnmodifiableIterator(Iterator iterator) {
            if (iterator == null) {
                throw new IllegalArgumentException("Iterator must not be null");
            }
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Object next() {
            return iterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Returns a predicated collection backed by the given collection.
     * Only objects that pass the test in the given predicate can be 
     * added to the collection.
     * It is important not to use the original collection after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param collection  the collection to predicate, must not be null
     * @param predicate  the predicate for the collection, must not be null
     * @return a predicated collection backed by the given collection
     * @throws IllegalArgumentException  if the Collection is null
     */
    public static Collection predicatedCollection(Collection collection, Predicate predicate) {
        return new PredicatedCollection(collection, predicate);
    }

}
