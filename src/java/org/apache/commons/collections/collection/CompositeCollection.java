/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/collection/CompositeCollection.java,v 1.1 2003/11/16 00:05:47 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.iterators.IteratorChain;

/**
 * Decorates a other collections to provide a single unified view.
 * <p>
 * Changes made to this collection will actually be made on the decorated collection.
 * Add and remove operations require the use of a pluggable strategy. If no 
 * strategy is provided then add and remove are unsupported.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/11/16 00:05:47 $
 *
 * @author Brian McCallister
 * @author Stephen Colebourne
 * @author Phil Steitz
 */
public class CompositeCollection implements Collection {
    
    /** CollectionMutator to handle changes to the collection */
    protected CollectionMutator mutator;
    
    /** Collections in the composite */
    protected Collection[] all;
    
    /**
     * Create an empty CompositeCollection.
     */
    public CompositeCollection() {
        super();
        this.all = new Collection[0];
    }
    
    /**
     * Create a Composite Collection with only coll composited.
     * 
     * @param coll  a collection to decorate
     */
    public CompositeCollection(Collection coll) {
        super();
        this.addComposited(coll);
    }
    
    /**
     * Create a CompositeCollection with colls as the initial list of
     * composited collections.
     * 
     * @param colls  an array of collections to decorate
     */
    public CompositeCollection(Collection[] colls) {
        super();
        this.addComposited(colls);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Gets the size of this composite collection.
     * <p>
     * This implementation calls <code>size()</code> on each collection.
     *
     * @return total number of elements in all contained containers
     */
    public int size() {
        int size = 0;
        for (int i = this.all.length - 1; i >= 0; i--) {
            size += this.all[i].size();
        }
        return size;
    }
    
    /**
     * Checks whether this composite collection is empty.
     * <p>
     * This implementation calls <code>isEmpty()</code> on each collection.
     *
     * @return true if all of the contained collections are empty
     */
    public boolean isEmpty() {
        for (int i = this.all.length - 1; i >= 0; i--) {
            if (this.all[i].isEmpty() == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks whether this composite collection contains the object.
     * <p>
     * This implementation calls <code>contains()</code> on each collection.
     *
     * @return true if obj is contained in any of the contained collections
     */
    public boolean contains(Object obj) {
        for (int i = this.all.length - 1; i >= 0; i--) {
            if (this.all[i].contains(obj)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets an iterator over all the collections in this composite.
     * <p>
     * This implementation uses an <code>IteratorChain</code>.
     *
     * @return an <code>IteratorChain</code> instance which supports
     *  <code>remove()</code>. Iteration occurs over contained collections in
     *  the order they were added, but this behavior should not be relied upon.
     * @see IteratorChain
     */
    public Iterator iterator() {
        if (this.all.length == 0) {
            return IteratorUtils.EMPTY_ITERATOR;
        }
        IteratorChain chain = new IteratorChain();
        for (int i = 0; i < this.all.length; ++i) {
            chain.addIterator(this.all[i].iterator());
        }
        return chain;
    }
    
    /**
     * Returns an array containing all of the elements in this composite.
     *
     * @return an object array of all the elements in the collection
     */
    public Object[] toArray() {
        final Object[] result = new Object[this.size()];
        int i = 0;
        for (Iterator it = this.iterator(); it.hasNext(); i++) {
            result[i] = it.next();
        }
        return result;
    }
    
    /**
     * Returns an object array, populating the supplied array if possible.
     * See <code>Collection</code> interface for full details.
     *
     * @return an array of all the elements in the collection
     */
    public Object[] toArray(Object array[]) {
        int size = this.size();
        Object[] result = null;
        if (array.length >= size) {
            result = array;
        }
        else {
            result = (Object[]) Array.newInstance(array.getClass().getComponentType(), size);
        }
        
        int offset = 0;
        for (int i = 0; i < this.all.length; ++i) {
            for (Iterator it = this.all[i].iterator(); it.hasNext();) {
                result[offset++] = it.next();
            }
        }
        if (result.length > size) {
            result[size] = null;
        }
        return result;
    }
    
    /**
     * Adds an object to the collection, throwing UnsupportedOperationException
     * unless a CollectionMutator strategy is specified.
     *
     * @param obj  the object to add
     * @return true if the collection was modified
     * @throws UnsupportedOperationException if CollectionMutator hasn't been set
     * @throws UnsupportedOperationException if add is unsupported
     * @throws ClassCastException if the object cannot be added due to its type
     * @throws NullPointerException if the object cannot be added because its null
     * @throws IllegalArgumentException if the object cannot be added
     */
    public boolean add(Object obj) {
        if (this.mutator == null) {
           throw new UnsupportedOperationException(
           "add() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.add(this, this.all, obj);
    }
    
    /**
     * Removes an object from the collection, throwing UnsupportedOperationException
     * unless a CollectionMutator strategy is specified.
     *
     * @param obj  the object being removed
     * @return true if the collection is changed
     * @throws UnsupportedOperationException if removed is unsupported
     * @throws ClassCastException if the object cannot be removed due to its type
     * @throws NullPointerException if the object cannot be removed because its null
     * @throws IllegalArgumentException if the object cannot be removed
     */
    public boolean remove(Object obj) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException(
            "remove() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.remove(this, this.all, obj);
    }
    
    /**
     * Checks whether this composite contains all the elements in the specified collection.
     * <p>
     * This implementation calls <code>contains()</code> for each element in the
     * specified collection.
     *
     * @param coll  the collection to check for
     * @return true if all elements contained
     */
    public boolean containsAll(Collection coll) {
        for (Iterator it = coll.iterator(); it.hasNext();) {
            if (this.contains(it.next()) == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Adds a collection of elements to this collection, throwing
     * UnsupportedOperationException unless a CollectionMutator strategy is specified.
     *
     * @param coll  the collection to add
     * @return true if the collection was modified
     * @throws UnsupportedOperationException if CollectionMutator hasn't been set
     * @throws UnsupportedOperationException if add is unsupported
     * @throws ClassCastException if the object cannot be added due to its type
     * @throws NullPointerException if the object cannot be added because its null
     * @throws IllegalArgumentException if the object cannot be added
     */
    public boolean addAll(Collection coll) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException(
            "addAll() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.addAll(this, this.all, coll);
    }
    
    /**
     * Removes the elements in the specified collection from this composite collection.
     * <p>
     * This implementation calls <code>removeAll</code> on each collection.
     *
     * @param coll  the collection to remove
     * @return true if the collection was modified
     * @throws UnsupportedOperationException if removeAll is unsupported
     */
    public boolean removeAll(Collection coll) {
        if (coll.size() == 0) {
            return false;
        }
        boolean changed = false;
        for (int i = this.all.length - 1; i >= 0; i--) {
            changed = (this.all[i].removeAll(coll) || changed);
        }
        return changed;
    }
    
    /**
     * Retains all the elements in the specified collection in this composite collection,
     * removing all others.
     * <p>
     * This implementation calls <code>retainAll()</code> on each collection.
     *
     * @param coll  the collection to remove
     * @return true if the collection was modified
     * @throws UnsupportedOperationException if retainAll is unsupported
     */
    public boolean retainAll(final Collection coll) {
        boolean changed = false;
        for (int i = this.all.length - 1; i >= 0; i--) {
            changed = (this.all[i].retainAll(coll) || changed);
        }
        return changed;
    }
    
    /**
     * Removes all of the elements from this collection .
     * <p>
     * This implementation calls <code>clear()</code> on each collection.
     *
     * @throws UnsupportedOperationException if clear is unsupported
     */
    public void clear() {
        for (int i = 0; i < this.all.length; ++i) {
            this.all[i].clear();
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Specify a CollectionMutator strategy instance to handle changes.
     *
     * @param mutator  the mutator to use
     */
    public void setMutator(CollectionMutator mutator) {
        this.mutator = mutator;
    }
    
    /**
     * Add these Collections to the list of collections in this composite
     *
     * @param comps Collections to be appended to the composite
     */
    public void addComposited(Collection[] comps) {
        ArrayList list = new ArrayList(Arrays.asList(this.all));
        list.addAll(Arrays.asList(comps));
        all = (Collection[]) list.toArray(new Collection[list.size()]);
    }
    
    /**
     * Add an additional collection to this composite.
     */
    public void addComposited(Collection c) {
        this.addComposited(new Collection[]{c});
    }
    
    /**
     * Add two additional collection to this composite.
     */
    public void addComposited(Collection c, Collection d) {
        this.addComposited(new Collection[]{c, d});
    }
    
    /**
     * Removes a collection from the those being decorated in this composite.
     *
     * @param coll  collection to be removed
     */
    public void removeComposited(Collection coll) {
        ArrayList list = new ArrayList(this.all.length);
        list.addAll(Arrays.asList(this.all));
        list.remove(coll);
        this.all = (Collection[]) list.toArray(new Collection[list.size()]);
    }
    
    /**
     * Returns a new collection containing all of the elements
     *
     * @return A new ArrayList containing all of the elements in this composite.
     *         The new collection is <i>not</i> backed by this composite.
     */
    public Collection toCollection() {
        return new ArrayList(this);
    }
    
    /**
     * Gets the collections being decorated.
     *
     * @return Unmodifiable collection of all collections in this composite.
     */
    public Collection getCollections() {
        return Collections.unmodifiableList(Arrays.asList(this.all));
    }
    
    //-----------------------------------------------------------------------
    /**
     * Pluggable strategy to handle changes to the composite.
     */
    public interface CollectionMutator {
        
        /**
         * Called when an object is to be added to the composite.
         *
         * @param composite  the CompositeCollection being changed
         * @param collections  all of the Collection instances in this CompositeCollection
         * @param obj  the object being added
         * @return true if the collection is changed
         * @throws UnsupportedOperationException if add is unsupported
         * @throws ClassCastException if the object cannot be added due to its type
         * @throws NullPointerException if the object cannot be added because its null
         * @throws IllegalArgumentException if the object cannot be added
         */
        public boolean add(CompositeCollection composite, Collection[] collections, Object obj);
        
        /**
         * Called when a collection is to be added to the composite.
         *
         * @param composite  the CompositeCollection being changed
         * @param collections  all of the Collection instances in this CompositeCollection
         * @param coll  the collection being added
         * @return true if the collection is changed
         * @throws UnsupportedOperationException if add is unsupported
         * @throws ClassCastException if the object cannot be added due to its type
         * @throws NullPointerException if the object cannot be added because its null
         * @throws IllegalArgumentException if the object cannot be added
         */
        public boolean addAll(CompositeCollection composite, Collection[] collections, Collection coll);
        
        /**
         * Called when an object is to be removed to the composite.
         *
         * @param composite  the CompositeCollection being changed
         * @param collections  all of the Collection instances in this CompositeCollection
         * @param obj  the object being removed
         * @return true if the collection is changed
         * @throws UnsupportedOperationException if removed is unsupported
         * @throws ClassCastException if the object cannot be removed due to its type
         * @throws NullPointerException if the object cannot be removed because its null
         * @throws IllegalArgumentException if the object cannot be removed
         */
        public boolean remove(CompositeCollection composite, Collection[] collections, Object obj);
        
    }
    
}

