/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/CollectionUtils.java,v 1.9 2002/08/10 00:36:34 pjack Exp $
 * $Revision: 1.9 $
 * $Date: 2002/08/10 00:36:34 $
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A set of {@link Collection} related utility methods.
 *
 * @author Rodney Waldhoff
 *
 * @since 1.0
 * @version $Id: CollectionUtils.java,v 1.9 2002/08/10 00:36:34 pjack Exp $
 */
public class CollectionUtils {

    /**
     * The empty iterator (immutable).
     */
    public static final Iterator EMPTY_ITERATOR = new EmptyIterator();

    /**
     * 'Hidden' class which acts as an EmptyIterator.
     * An alternative is to use: Collections.EMPTY_LIST.iterator();
     * however that will create a new iterator object each time.
     */
    private static class EmptyIterator implements Iterator {
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
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
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        Iterator it = a.iterator();
        while(it.hasNext()) {
            Object obj = it.next();
            if(getFreq(obj,mapa) > getFreq(obj,mapb)) {
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
        Iterator it = col.iterator();
        while(it.hasNext()) {
            Object elt = it.next();
            if((null == obj && null == elt) || obj.equals(elt)) {
                count++;
            }
        }
        return count;
    }

    
    
    
    /** Finds the first element in the given collection which matches the given predicate
      *
      * @return the first element of the collection which matches the predicate or null if none could be found
      */
    public static Object find( Collection collection, Predicate predicate ) {
        if ( collection != null && predicate != null ) {            
            for ( Iterator iter = collection.iterator(); iter.hasNext(); ) {
                Object item = iter.next();
                if ( predicate.evaluate( item ) ) {
                    return item;
                }
            }
        }
        return null;
    }
    
    /** Executes the given closure on each element in the colleciton
      */
    public static void forAllDo( Collection collection, Closure closure) {
        if ( collection != null ) {
            for ( Iterator iter = collection.iterator(); iter.hasNext(); ) {
                Object element = iter.next();
                closure.execute( element );
            }
        }
    }

    /** Selects all elements from inputCollection which match the given predicate
      * into an output collection
      */
    public static Collection select( Collection inputCollection, Predicate predicate ) {
        ArrayList answer = new ArrayList( inputCollection.size() );
        select( inputCollection, predicate, answer );
        return answer;
    }
    
    /** Selects all elements from inputCollection which match the given predicate
      * and adds them to outputCollection
      */
    public static void select( Collection inputCollection, Predicate predicate, Collection outputCollection ) {
        if ( inputCollection != null && predicate != null ) {            
            for ( Iterator iter = inputCollection.iterator(); iter.hasNext(); ) {
                Object item = iter.next();
                if ( predicate.evaluate( item ) ) {
                    outputCollection.add( item );
                }
            }
        }
    }
    
    /** Transforms all elements from inputCollection with the given transformer 
      * and adds them to the outputCollection
      */
    public static Collection collect( Collection inputCollection, Transformer transformer ) {
        ArrayList answer = new ArrayList( inputCollection.size() );
        collect( inputCollection, transformer, answer );
        return answer;
    }
    
    /** Transforms all elements from the inputIterator  with the given transformer 
      * and adds them to the outputCollection
      */
    public static Collection collect( Iterator inputIterator, Transformer transformer ) {
        ArrayList answer = new ArrayList();
        collect( inputIterator, transformer, answer );
        return answer;
    }
    
    /** Transforms all elements from inputCollection with the given transformer 
      * and adds them to the outputCollection
      *
      * @return the outputCollection
      */
    public static Collection collect( Collection inputCollection, final Transformer transformer, final Collection outputCollection ) {
        if ( inputCollection != null ) {
            return collect( inputCollection.iterator(), transformer, outputCollection );
        }
        return outputCollection;
    }

    /** Transforms all elements from the inputIterator with the given transformer 
      * and adds them to the outputCollection
      *
      * @return the outputCollection
      */
    public static Collection collect( Iterator inputIterator, final Transformer transformer, final Collection outputCollection ) {
        if ( inputIterator != null && transformer != null ) {            
            while ( inputIterator.hasNext() ) {
                Object item = inputIterator.next();
                Object value = transformer.transform( item );
                outputCollection.add( value );
            }
        }
        return outputCollection;
    }

    /** Adds all elements in the iteration to the given collection 
      */
    public static void addAll( Collection collection, Iterator iterator ) {
        while ( iterator.hasNext() ) {
            collection.add( iterator.next() );
        }
    }
    
    /** Adds all elements in the enumeration to the given collection 
      */
    public static void addAll( Collection collection, Enumeration enumeration ) {
        while ( enumeration.hasMoreElements() ) {
            collection.add( enumeration.nextElement() );
        }
    }    
    
    /** Adds all elements in the array to the given collection 
      */
    public static void addAll( Collection collection, Object[] elements ) {
        for ( int i = 0, size = elements.length; i < size; i++ ) {
            collection.add( elements[i] );
        }
    }    
    
    /**
     * Given an Object, and an index, it will get the nth value in the
     * object.
     */
    public static Object index(Object obj, int idx) {
        return index(obj, new Integer(idx));
    }
    
    /**
     * Given an Object, and an index, it will get the nth value in the
     * object.
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

    /** Returns an Iterator for the given object. Currently this method can handle
     * Iterator, Enumeration, Collection, Map, Object[] or array */
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


    /** Reverses the order of the given array */
    public static void reverseArray(Object[] array) {
        int i = 0;
        int j = array.length - 1;
        Object tmp;
        
        while(j>i) {
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
}
