/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/PredicateUtils.java,v 1.2 2002/06/16 03:39:40 mas Exp $
 * $Revision: 1.2 $
 * $Date: 2002/06/16 03:39:40 $
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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map;
import java.util.SortedMap;
/**
 * PredicateUtils provides access to common predicate functionality.
 * <p>
 * Included are collections wrapper that support validation.
 * Only elements that pass a predicate (validation test) can 
 * be added to the collection. An <tt>IllegalArgumentException</tt> is
 * thrown if the validation fails.
 * <p>
 * The collections can be accessed by static factory methods. A wrapper
 * is provided for all the java and commons collections.
 * <p>
 * Also included are predicate implementations for True, False, Not,
 * And, Or and instanceof.
 * 
 * @author Stephen Colebourne
 */
public class PredicateUtils {
    
	/**
	 * A predicate that always returns true
	 */    
    public static final Predicate TRUE_PREDICATE = new TruePredicate();
	/**
	 * A predicate that always returns false
	 */    
    public static final Predicate FALSE_PREDICATE = new FalsePredicate();
    
	/**
	 * Restructive constructor
	 */
	private PredicateUtils() {
	    super();
	}

	/**
	 * Create a new Collection that wraps another Collection and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original collection is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param coll  the collection to wrap and restrict
	 * @param predicate  the predicate to control what to allow into the collection
	 */
	public static Collection predicateCollection(Collection coll, Predicate predicate) {
         return new PredicateCollection(coll, predicate);
    }

	/**
	 * Create a new List that wraps another List and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original list is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param list  the list to wrap and restrict
	 * @param predicate  the predicate to control what to allow into the list
	 */
	public static List predicateList(List list, Predicate predicate) {
         return new PredicateList(list, predicate);
    }

	/**
	 * Create a new Set that wraps another Set and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original set is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param set  the set to wrap and restrict
	 * @param predicate  the predicate to control what to allow into the set
	 */
	public static Set predicateSet(Set set, Predicate predicate) {
         return new PredicateSet(set, predicate);
    }

	/**
	 * Create a new SortedSet that wraps another SortedSet and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original set is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param set  the set to wrap and restrict
	 * @param predicate  the predicate to control what to allow into the set
	 */
	public static SortedSet predicateSortedSet(SortedSet set, Predicate predicate) {
         return new PredicateSortedSet(set, predicate);
    }

	/**
	 * Create a new Bag that wraps another Bag and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original bag is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param bag  the bag to wrap and restrict
	 * @param predicate  the predicate to control what to allow into the bag
	 */
	public static Bag predicateBag(Bag bag, Predicate predicate) {
         return new PredicateBag(bag, predicate);
    }

	/**
	 * Create a new SortedBag that wraps another SortedBag and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original bag is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param bag  the bag to wrap and restrict
	 * @param predicate  the predicate to control what to allow into the bag
	 */
	public static SortedBag predicateSortedBag(SortedBag bag, Predicate predicate) {
         return new PredicateSortedBag(bag, predicate);
    }

	/**
	 * Create a new Map that wraps another Map and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original map is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param map  the map to wrap and restrict
	 * @param keyPredicate  the predicate to control what to allow into the bag
	 * @param valuePredicate  the predicate to control what to allow into the bag
	 */
	public static Map predicateMap(Map map, Predicate keyPredicate, Predicate valuePredicate) {
         return new PredicateMap(map, keyPredicate, valuePredicate);
    }

	/**
	 * Create a new SortedMap that wraps another SortedMap and validates
	 * entries. Only objects that pass the test in the predicate can be
	 * added to the list.
	 * It is important that the original map is not used again after
	 * this call, as it is a backdoor to add non-validated objects.
	 * @param map  the map to wrap and restrict
	 * @param keyPredicate  the predicate to control what to allow into the bag
	 * @param valuePredicate  the predicate to control what to allow into the bag
	 */
	public static SortedMap predicateSortedMap(SortedMap map, Predicate keyPredicate, Predicate valuePredicate) {
         return new PredicateSortedMap(map, keyPredicate, valuePredicate);
    }

	/**
	 * Create a new predicate that returns true only if both of the passed
	 * in predicates are true.
	 * @param predicate1  the first predicate
	 * @param predicate2  the second predicate
	 */
	public static Predicate andPredicate(Predicate predicate1, Predicate predicate2) {
         return new AndPredicate(predicate1, predicate2);
    }

	/**
	 * Create a new predicate that returns true if either of the passed
	 * in predicates are true.
	 * @param predicate1  the first predicate
	 * @param predicate2  the second predicate
	 */
	public static Predicate orPredicate(Predicate predicate1, Predicate predicate2) {
         return new OrPredicate(predicate1, predicate2);
    }

	/**
	 * Create a new predicate that returns true if the passed in predicate
	 * returns false and vice versa.
	 * @param predicate  the predicate to not
	 */
	public static Predicate notPredicate(Predicate predicate) {
         return new NotPredicate(predicate);
    }

	/**
	 * Create a new predicate that checks if the object passed in is of
	 * a particular type.
	 * @param type  the type to check for
	 */
	public static Predicate instanceofPredicate(Class type) {
         return new InstanceofPredicate(type);
    }

	/**
	 * Perform the validation against the predicate.
	 * @param object  object to be validated
	 */	
	private static void validate(Predicate predicate, Object object) {
	    if (predicate.evaluate(object) == false) {
	        throw new IllegalArgumentException("Predicate validation: " +
	        	object + " cannot be added to the list");
	    }
	}

	/**
	 * PredicateCollection validates a Collection
	 */
	private static class PredicateCollection 
			implements Collection, Serializable {
			    
        /** The predicate to control entry into the collection */
        protected final Predicate iPredicate;
        /** The collection being wrapped */
        protected final Collection iCollection;
			    
    	/**
    	 * Create a new PredicateCollection that wraps another collection.
    	 * It is important that the original collection is not used again
    	 * after this call, as it is a backdoor to add non-validated objects.
    	 * @param coll  the collection to wrap and restrict
    	 * @param predicate  the predicate used to validate entry into the list
    	 */
    	public PredicateCollection(Collection coll, Predicate predicate) {
    	    super();
    	    if (coll == null) {
    	        throw new IllegalArgumentException("Collection to be wrapped must not be null");
    	    }
    	    if (predicate == null) {
    	        throw new IllegalArgumentException("Predicate must not be null");
    	    }
    	    iPredicate = predicate;
    	    iCollection = coll;
            Iterator it = iCollection.iterator();
            while (it.hasNext()) {
                validate(iPredicate, it.next());
            }
    	}
    
        /**
         * Add an item to the end of the list. If the item is not an instance
         * of the list's validation type an exception is thrown. The state of
         * the list will be unaltered if an exception is thrown.
         * @see Collection#add(Object)
         * @param item  the item to add
         * @throws IllegalArgumentException if the object is not of a valid type
         */
        public boolean add(Object item) {
            validate(iPredicate, item);
            return iCollection.add(item);
        }
    
        /**
         * Add a collection to the end of the list. If any of the items in the
         * collection is not an instance of the list's validation type an
         * exception is thrown. The state of the list will be unaltered if an
         * exception is thrown.
         * @see Collection#addAll(Collection)
         * @param coll  the collection to add
         * @throws IllegalArgumentException if the object is not of a valid type
         */
        public boolean addAll(Collection coll) {
            Iterator it = coll.iterator();
            while (it.hasNext()) {
    	        validate(iPredicate, it.next());
            }
            return iCollection.addAll(coll);
        }
    
        /**
         * @see Collection#iterator()
         */
        public Iterator iterator() {
            return iCollection.iterator();
        }

        /**
         * @see Collection#size()
         */
        public int size() {
            return iCollection.size();
        }

        /**
         * @see Collection#clear()
         */
        public void clear() {
            iCollection.clear();
        }

        /**
         * @see Collection#isEmpty()
         */
        public boolean isEmpty() {
            return iCollection.isEmpty();
        }

        /**
         * @see Collection#contains(Object)
         */
        public boolean contains(Object item) {
            return iCollection.contains(item);
        }

        /**
         * @see Collection#containsAll(Collection)
         */
        public boolean containsAll(Collection coll) {
            return iCollection.containsAll(coll);
        }

        /**
         * @see Collection#remove(Object)
         */
        public boolean remove(Object item) {
            return iCollection.remove(item);
        }

        /**
         * @see Collection#removeAll(Collection)
         */
        public boolean removeAll(Collection coll) {
            return iCollection.remove(coll);
        }

        /**
         * @see Collection#retainAll(Collection)
         */
        public boolean retainAll(Collection coll) {
            return iCollection.retainAll(coll);
        }

        /**
         * @see Collection#toArray()
         */
        public Object[] toArray() {
            return iCollection.toArray();
        }

        /**
         * @see Collection#toArray(Object[])
         */
        public Object[] toArray(Object[] array) {
            return iCollection.toArray(array);
        }

        /**
         * @see Object#equals(Object)
         */
        public boolean equals(Object obj) {
            return iCollection.equals(obj);
        }
    
        /**
         * @see Object#hashCode()
         */
        public int hashCode() {
            return iCollection.hashCode();
        }
    
        /**
         * @see Object#toString()
         */
        public String toString() {
            return iCollection.toString();
        }
	}
	
	/**
	 * PredicateList validates a List
	 */
	private static class PredicateList
			extends PredicateCollection
			implements List {
	
    	/**
    	 * Create a new PredicateList that wraps another list.
    	 * It is important that the original list is not used again after
    	 * this call, as it is a backdoor to add non-validated objects.
    	 * @param list  the list to wrap and restrict
    	 * @param predicate  the predicate used to validate entry into the list
    	 */
    	public PredicateList(List list, Predicate predicate) {
    	    super(list, predicate);
    	}
    	
        /**
         * Add an item to the list at the specified index. If the item is
         * not an instance of the list's validation type an exception is
         * thrown. The state of the list will be unaltered if an exception 
         * is thrown.
         * @see List#add(int, Object)
         * @param index  the index at which to add the item
         * @param item  the item to add
         * @throws IllegalArgumentException if the object is not of a valid type
         */
        public void add(int index, Object item) {
            validate(iPredicate, item);
            ((List) iCollection).add(index, item);
        }
    
        /**
         * Add a collection at the specified index. If any of the items in the
         * collection is not an instance of the list's validation type an
         * exception is thrown. The state of the list will be unaltered if an
         * exception is thrown.
         * @see List#addAll(int, Collection)
         * @param index  the index at which to add the collection
         * @param coll  the collection to add
         * @throws IllegalArgumentException if the object is not of a valid type
         */
        public boolean addAll(int index, Collection coll) {
            Iterator it = coll.iterator();
            while (it.hasNext()) {
    	        validate(iPredicate, it.next());
            }
            return ((List) iCollection).addAll(index, coll);
        }
    
        /**
         * Set the value at the specified index. If the item is not an instance
         * of the list's validation type an exception is thrown. The state of
         * the list will be unaltered if an exception is thrown.
         * @see List#set(int, Object)
         * @param index  the index to change
         * @param item  the item to change to
         * @throws IllegalArgumentException if the object is not of a valid type
         */
        public Object set(int index, Object item) {
            validate(iPredicate, item);
            return ((List) iCollection).set(index, item);
        }
        
        /**
         * @see List#listIterator()
         */
        public ListIterator listIterator() {
            return new PredicateListIterator(((List) iCollection).listIterator(), iPredicate);
        }
    
        /**
         * @see List#listIterator(int)
         */
        public ListIterator listIterator(int index) {
            return new PredicateListIterator(((List) iCollection).listIterator(index), iPredicate);
        }
    
        /**
         * @see List#subList(int, int)
         */
        public List subList(int fromIndex, int toIndex) {
            return new PredicateList(((List) iCollection).subList(fromIndex, toIndex), iPredicate);
        }

        /**
         * @see List#get(int)
         */
        public Object get(int index) {
            return ((List) iCollection).get(index);
        }
    
        /**
         * @see List#indexOf(Object)
         */
        public int indexOf(Object item) {
            return ((List) iCollection).indexOf(item);
        }
    
        /**
         * @see List#lastIndexOf(Object)
         */
        public int lastIndexOf(Object item) {
            return ((List) iCollection).lastIndexOf(item);
        }
    
        /**
         * @see List#remove(int)
         */
        public Object remove(int index) {
            return ((List) iCollection).remove(index);
        }
	}

	/**
	 * PredicateListIterator handles the list iterator for PredicateList
	 */
	private static class PredicateListIterator implements ListIterator {
	    
	    private final ListIterator iIterator;
	    private final Predicate iPredicate;
	    
	    /**
	     * Constructor
	     */
	    private PredicateListIterator(ListIterator iterator, Predicate predicate) {
	        super();
	        iIterator = iterator;
	        iPredicate = predicate;
		}
		
        /**
         * @see Iterator#hasNext()
         */
        public boolean hasNext() {
            return iIterator.hasNext();
        }

        /**
         * @see ListIterator#hasPrevious()
         */
        public boolean hasPrevious() {
            return iIterator.hasPrevious();
        }

        /**
         * @see Iterator#next()
         */
        public Object next() {
            return iIterator.next();
        }

        /**
         * @see ListIterator#nextIndex()
         */
        public int nextIndex() {
            return iIterator.nextIndex();
        }

        /**
         * @see ListIterator#previous()
         */
        public Object previous() {
            return iIterator.previous();
        }

        /**
         * @see ListIterator#previousIndex()
         */
        public int previousIndex() {
            return iIterator.previousIndex();
        }

        /**
         * @see Iterator#remove()
         */
        public void remove() {
            iIterator.remove();
        }

        /**
         * @see ListIterator#add(Object)
         */
        public void add(Object item) {
	        validate(iPredicate, item);
            iIterator.add(item);
        }

        /**
         * @see ListIterator#set(Object)
         */
        public void set(Object item) {
	        validate(iPredicate, item);
            iIterator.set(item);
        }
	}
	
	/**
	 * PredicateSet validates a Set
	 */
	private static class PredicateSet
			extends PredicateCollection
			implements Set {
	
    	/**
    	 * Create a new PredicateSet that wraps another Set.
    	 * It is important that the original set is not used again after
    	 * this call, as it is a backdoor to add non-validated objects.
    	 * @param set  the set to wrap and restrict
    	 * @param predicate  the predicate used to validate entry into the set
    	 */
    	public PredicateSet(Set set, Predicate predicate) {
    	    super(set, predicate);
    	}
	}
	
	/**
	 * PredicateSet validates a SortedSet
	 */
	private static class PredicateSortedSet
			extends PredicateSet
			implements SortedSet {
	
    	/**
    	 * Create a new PredicateSortedSet that wraps another SortedSet.
    	 * It is important that the original SortedSet is not used again after
    	 * this call, as it is a backdoor to add non-validated objects.
    	 * @param set  the set to wrap and restrict
    	 * @param predicate  the predicate used to validate entry into the SortedSet
    	 */
    	public PredicateSortedSet(SortedSet set, Predicate predicate) {
    	    super(set, predicate);
    	}
    	
        /**
         * @see SortedSet#headSet(Object)
         */
        public SortedSet headSet(Object toElement) {
            return new PredicateSortedSet(((SortedSet) iCollection).headSet(toElement), iPredicate);
        }
    
        /**
         * @see SortedSet#subSet(Object, Object)
         */
        public SortedSet subSet(Object fromElement, Object toElement) {
            return new PredicateSortedSet(((SortedSet) iCollection).subSet(fromElement, toElement), iPredicate);
        }
    
        /**
         * @see SortedSet#tailSet(Object)
         */
        public SortedSet tailSet(Object fromElement) {
            return new PredicateSortedSet(((SortedSet) iCollection).tailSet(fromElement), iPredicate);
        }
    
        /**
         * @see SortedSet#first()
         */
        public Object first() {
            return ((SortedSet) iCollection).first();
        }
    
        /**
         * @see SortedSet#last()
         */
        public Object last() {
            return ((SortedSet) iCollection).last();
        }
        
        /**
         * @see SortedSet#comparator()
         */
        public Comparator comparator() {
            return ((SortedSet) iCollection).comparator();
        }
	}
	
	/**
	 * PredicateBag validates a Bag
	 */
	private static class PredicateBag
			extends PredicateCollection
			implements Bag {
	
    	/**
    	 * Create a new PredicateBag that wraps another Bag.
    	 * It is important that the original Bag is not used again after
    	 * this call, as it is a backdoor to add non-validated objects.
    	 * @param bag  the bag to wrap and restrict
    	 * @param predicate  the predicate used to validate entry into the Bag
    	 */
    	public PredicateBag(Bag bag, Predicate predicate) {
    	    super(bag, predicate);
    	}
    	
        /**
         * @see Bag#add(Object, int)
         */
        public boolean add(Object item, int i) {
            validate(iPredicate, item);
            return ((Bag) iCollection).add(item, i);
        }

        /**
         * @see Bag#getCount(Object)
         */
        public int getCount(Object item) {
            return ((Bag) iCollection).getCount(item);
        }

        /**
         * @see Bag#remove(Object, int)
         */
        public boolean remove(Object item, int i) {
            return ((Bag) iCollection).remove(item, i);
        }

        /**
         * @see Bag#uniqueSet()
         */
        public Set uniqueSet() {
            return ((Bag) iCollection).uniqueSet();
        }
	}
	
	/**
	 * PredicateSortedBag validates a SortedBag
	 */
	private static class PredicateSortedBag
			extends PredicateBag
			implements SortedBag {
	
    	/**
    	 * Create a new PredicateSortedBag that wraps another SortedBag.
    	 * It is important that the original SortedBag is not used again after
    	 * this call, as it is a backdoor to add non-validated objects.
    	 * @param bag  the bag to wrap and restrict
    	 * @param predicate  the predicate used to validate entry into the SortedBag
    	 */
    	public PredicateSortedBag(SortedBag bag, Predicate predicate) {
    	    super(bag, predicate);
    	}
    	
        /**
         * @see SortedBag#comparator()
         */
        public Comparator comparator() {
            return ((SortedBag) iCollection).comparator();
        }

        /**
         * @see SortedBag#first()
         */
        public Object first() {
            return ((SortedBag) iCollection).first();
        }

        /**
         * @see SortedBag#last()
         */
        public Object last() {
            return ((SortedBag) iCollection).last();
        }
	}
	
	/**
	 * PredicateBag validates a Map
	 */
	private static class PredicateMap
			implements Map {
	
        /** The predicate to control entry into the map */
        protected final Predicate iKeyPredicate;
        /** The predicate to control entry into the map */
        protected final Predicate iValuePredicate;
        /** The list being wrapped */
        protected final Map iMap;
			    
    	/**
    	 * Create a new PredicateMap that wraps another Map.
    	 * It is important that the original Map is not used again after
    	 * this call, as it is a backdoor to add non-validated objects.
    	 * @param map  the map to wrap and restrict
    	 * @param keyPredicate  the predicate used to validate entry into the SortedMap
    	 * @param valuePredicate  the predicate used to validate entry into the SortedMap
    	 */
    	public PredicateMap(Map map, Predicate keyPredicate, Predicate valuePredicate) {
    	    super();
    	    if (map == null) {
    	        throw new IllegalArgumentException("Collection to be wrapped must not be null");
    	    }
    	    if (keyPredicate == null) {
    	        throw new IllegalArgumentException("Key Predicate must not be null");
    	    }
    	    if (valuePredicate == null) {
    	        throw new IllegalArgumentException("Value Predicate must not be null");
    	    }
    	    iKeyPredicate = keyPredicate;
    	    iValuePredicate = valuePredicate;
    	    iMap = map;
    	    for (Iterator it = iMap.keySet().iterator(); it.hasNext();) {
                validate(iKeyPredicate, it.next());
            }
    	    for (Iterator it = iMap.values().iterator(); it.hasNext();) {
                validate(iValuePredicate, it.next());
            }
    	}
    	
        /**
         * @see Map#put(Object, Object)
         */
        public Object put(Object key, Object value) {
            validate(iKeyPredicate, key);
            validate(iValuePredicate, value);
            return iMap.put(key, value);
        }

        /**
         * @see Map#putAll(Map)
         */
        public void putAll(Map map) {
            for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
	            validate(iKeyPredicate, entry.getKey());
    	        validate(iValuePredicate, entry.getValue());
            }
            iMap.putAll(map);
        }

        /**
         * @see Map#entrySet()
         */
        public Set entrySet() {
            return new PredicateMapEntrySet(iMap.entrySet(), iValuePredicate);
        }

        /**
         * @see Map#keySet()
         */
        public Set keySet() {
            return new PredicateSet(iMap.keySet(), iKeyPredicate);
        }

        /**
         * @see Map#values()
         */
        public Collection values() {
            return new PredicateCollection(iMap.values(), iValuePredicate);
        }

        /**
         * @see Map#get(Object)
         */
        public Object get(Object key) {
            return iMap.get(key);
        }

        /**
         * @see Map#size()
         */
        public int size() {
            return iMap.size();
        }

        /**
         * @see Map#clear()
         */
        public void clear() {
            iMap.clear();
        }

        /**
         * @see Map#isEmpty()
         */
        public boolean isEmpty() {
            return iMap.isEmpty();
        }

        /**
         * @see Map#containsKey(Object)
         */
        public boolean containsKey(Object key) {
            return iMap.containsKey(key);
        }

        /**
         * @see Map#containsValue(Object)
         */
        public boolean containsValue(Object value) {
            return iMap.containsValue(value);
        }

        /**
         * @see Map#remove(Object)
         */
        public Object remove(Object key) {
            return iMap.remove(key);
        }

        /**
         * @see Object#equals(Object)
         */
        public boolean equals(Object obj) {
            return iMap.equals(obj);
        }
    
        /**
         * @see Object#hashCode()
         */
        public int hashCode() {
            return iMap.hashCode();
        }
    
        /**
         * @see Object#toString()
         */
        public String toString() {
            return iMap.toString();
        }
	}   
	
	/**
	 * PredicateSortedBag validates a SortedMap
	 */
	private static class PredicateSortedMap
			extends PredicateMap
			implements SortedMap {
	
    	/**
    	 * Create a new PredicateSortedMap that wraps another SortedMap.
    	 * It is important that the original SortedBag is not used again after
    	 * this call, as it is a backdoor to add non-validated objects.
    	 * @param bag  the bag to wrap and restrict
    	 * @param keyPredicate  the predicate used to validate entry into the SortedMap
    	 * @param valuePredicate  the predicate used to validate entry into the SortedMap
    	 */
    	public PredicateSortedMap(SortedMap map, Predicate keyPredicate, Predicate valuePredicate) {
    	    super(map, keyPredicate, valuePredicate);
    	}
    	
        /**
         * @see SortedMap#comparator()
         */
        public Comparator comparator() {
            return ((SortedMap) iMap).comparator();
        }

        /**
         * @see SortedMap#firstKey()
         */
        public Object firstKey() {
            return ((SortedMap) iMap).firstKey();
        }

        /**
         * @see SortedMap#lastKey()
         */
        public Object lastKey() {
            return ((SortedMap) iMap).lastKey();
        }

        /**
         * @see SortedMap#headMap(Object)
         */
        public SortedMap headMap(Object toKey) {
            return new PredicateSortedMap(
            	((SortedMap) iMap).headMap(toKey), iKeyPredicate, iValuePredicate);
        }

        /**
         * @see SortedMap#tailMap(Object)
         */
        public SortedMap tailMap(Object fromKey) {
            return new PredicateSortedMap(
            	((SortedMap) iMap).tailMap(fromKey), iKeyPredicate, iValuePredicate);
        }

        /**
         * @see SortedMap#subMap(Object, Object)
         */
        public SortedMap subMap(Object fromKey, Object toKey) {
            return new PredicateSortedMap(
            	((SortedMap) iMap).subMap(fromKey, toKey), iKeyPredicate, iValuePredicate);
        }
	}
	
	/**
	 * Map helper class to access iterator
	 */
	public static class PredicateMapEntrySet
	        extends AbstractSet {
	    private final Set iSet;
        private final Predicate iValuePredicate;
	    
	    /**
	     * Constructor
	     */
	    private PredicateMapEntrySet(Set set, Predicate predicate) {
	        super();
	        iSet = set;
	        iValuePredicate = predicate;
	    }
            
        /**
         * @see Collection#clear()
         */
        public void clear() {
            iSet.clear();
        }

        /**
         * @see Collection#iterator()
         */
        public Iterator iterator() {
            return new PredicateMapEntrySetIterator(iSet.iterator(), iValuePredicate);
        }

        /**
         * @see Collection#remove(Object)
         */
        public boolean remove(Object obj) {
            return iSet.remove(obj);
        }

        /**
         * @see Collection#size()
         */
        public int size() {
            return iSet.size();
        }

	}
	
	/**
	 * Iterator to protect the setValue method of Map.Entry
	 */
    public static class PredicateMapEntrySetIterator
    		implements Iterator {
	    private final Iterator iIterator;
        private final Predicate iValuePredicate;
	    
	    /**
	     * Constructor
	     */
	    private PredicateMapEntrySetIterator(Iterator iterator, Predicate predicate) {
	        super();
	        iIterator = iterator;
	        iValuePredicate = predicate;
	    }
            
        /**
         * @see Iterator#next()
         */
        public Object next() {
            Object obj = iIterator.next();
            return new PredicateMapEntry((Map.Entry) obj, iValuePredicate);
        }

        /**
         * @see Iterator#hasNext()
         */
        public boolean hasNext() {
            return iIterator.hasNext();
        }

        /**
         * @see Iterator#remove()
         */
        public void remove() {
            iIterator.remove();
        }

    }
    
	/**
	 * MapEntry to protect the setValue method
	 */
    public static class PredicateMapEntry
    		implements Map.Entry {
	    private final Map.Entry iEntry;
        private final Predicate iValuePredicate;
        
	    /**
	     * Constructor
	     */
	    private PredicateMapEntry(Map.Entry entry, Predicate predicate) {
	        super();
	        iEntry = entry;
	        iValuePredicate = predicate;
	    }
	    
        /**
         * @see java.util.Map.Entry#getKey()
         */
        public Object getKey() {
            return iEntry.getKey();
        }

        /**
         * @see java.util.Map.Entry#getValue()
         */
        public Object getValue() {
            return iEntry.getValue();
        }

        /**
         * @see java.util.Map.Entry#setValue(Object)
         */
        public Object setValue(Object object) {
            validate(iValuePredicate, object);
            return iEntry.setValue(object);
        }

    }
    
	/**
	 * True predicate implementation
	 */    
    private static class TruePredicate implements Predicate {
        private TruePredicate() {
            super();
        }
	    public boolean evaluate(Object input) {
	        return true;
	    }
    }
    
	/**
	 * False predicate implementation
	 */    
    private static class FalsePredicate implements Predicate {
        private FalsePredicate() {
            super();
        }
	    public boolean evaluate(Object input) {
	        return false;
	    }
    }
    
	/**
	 * And predicate implementation
	 */    
    private static class AndPredicate implements Predicate {
        private final Predicate iPredicate1;
        private final Predicate iPredicate2;
        
        /**
         * Constructor
         */
        private AndPredicate(Predicate predicate1, Predicate predicate2) {
            super();
    	    if ((predicate1 == null) || (predicate2 == null)) {
    	        throw new IllegalArgumentException("Predicate must not be null");
    	    }
            iPredicate1 = predicate1;
            iPredicate2 = predicate2;
        }
	    public boolean evaluate(Object input) {
	        return iPredicate1.evaluate(input) && iPredicate2.evaluate(input);
	    }
    }
    
	/**
	 * Or predicate implementation
	 */    
    private static class OrPredicate implements Predicate {
        private final Predicate iPredicate1;
        private final Predicate iPredicate2;
        
        /**
         * Constructor
         */
        private OrPredicate(Predicate predicate1, Predicate predicate2) {
            super();
    	    if ((predicate1 == null) || (predicate2 == null)) {
    	        throw new IllegalArgumentException("Predicate must not be null");
    	    }
            iPredicate1 = predicate1;
            iPredicate2 = predicate2;
        }
	    public boolean evaluate(Object input) {
	        return iPredicate1.evaluate(input) || iPredicate2.evaluate(input);
	    }
    }
    
	/**
	 * Not predicate implementation
	 */    
    private static class NotPredicate implements Predicate {
        private final Predicate iPredicate;
        
        /**
         * Constructor
         */
        private NotPredicate(Predicate predicate) {
            super();
    	    if (predicate == null) {
    	        throw new IllegalArgumentException("Predicate must not be null");
    	    }
            iPredicate = predicate;
        }
	    public boolean evaluate(Object input) {
	        return ! iPredicate.evaluate(input);
	    }
    }
    
    /**
     * Predicate that checks the type of an object
     */
    public static class InstanceofPredicate implements Predicate {
        private final Class iType;
    
    	/**
    	 * Constructor
    	 * @param type  the type to validate for
    	 */
    	public InstanceofPredicate(Class type) {
    	    super();
    	    if (type == null) {
    	        throw new IllegalArgumentException("Type to be checked for must not be null");
    	    }
    	    iType = type;
    	}
    
        /**
         * Validate the input object to see if it is an instanceof the 
         * type of the predicate.
         * @param object  the object to be checked
         * @return true if it is an instance
         */
        public boolean evaluate(Object object) {
            return iType.isInstance(object);
        }
    }
}

