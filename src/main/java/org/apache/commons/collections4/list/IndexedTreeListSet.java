/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Data structure which implements both <code>List</code> and <code>Set</code>.
 * <p/>
 * As a <code>List</code> this data structure stores order of elements and
 * provides access by index. It's is optimised for fast insertions, removals
 * and searching by any index or object in the list.
 * As a <code>Set</code> this data structure stores unique elements only.
 * <p/>
 * IndexedTreeListSet can be suitable for tasks which requires fast modification
 * in the middle of a list and provides fast contains and indexOf operations.
 * <p/>
 * Get by index, insert (head, tail, middle), remove(by index or by value)
 * and indexOf are all O(log n). Contains is O(1) or O(log n) depending on Map
 * implementation.
 * <p/>
 * Internally it uses Map (HashMap by default) and AVL tree.
 * HashMap can be replaced to TreeMap, this will slightly reduce overall performance
 * but will eliminate problems with hash collisions and hash table resizing.
 * <p/>
 * Objects equality is checked by Map, so objects should be immutable for Map
 * consistency.
 * <p/>
 * Code is based on apache common collections <code>TreeList</code>.
 * Comparing to <code>TreeList</code> this data structure:
 * <ul>
 * <li>Contains unique elements</li>
 * <li>Has almost the same or slightly slower insertion/removing operations, O(log n)</li>
 * <li>Requires more memory, however it's still O(n).</li>
 * <li>Has greatly improved contains and indexOf operations, O(log n) while TreeList has O(n)</li>
 * </ul>
 *
 * @author Aleksandr Maksymenko
 */
public class IndexedTreeListSet<E> extends AbstractIndexedTreeList<E> implements Set<E> {

    private static final boolean SUPPORT_ADD_SET_IN_ITERATOR = false;

    /** Map from element to it's node or nodes */
    protected final Map<E, AVLNode> nodeMap;

    //-----------------------------------------------------------------------
    /**
     * Constructs a new empty list.
     */
    public IndexedTreeListSet() {
        this(new HashMap<>());
    }

    /**
     * Constructs a new empty list.
     * @param map Map implementation. It defines how elements would be compared. For example HashMap (by hashcode/equals),
     *            TreeMap (by compareTo or Comparator), IdentityHashMap (by identity). Specified map should be empty.
     */
    public IndexedTreeListSet(final Map map) {
        this.nodeMap = map;
        this.supportAddSetInIterator = SUPPORT_ADD_SET_IN_ITERATOR;
    }

    /**
     * Constructs a new list that copies the specified collection.
     *
     * @param coll The collection to copy
     * @throws NullPointerException if the collection is null
     */
    public IndexedTreeListSet(final Collection<? extends E> coll) {
        this(coll, new HashMap<>());
    }

    /**
     * Constructs a new list that copies the specified collection.
     *
     * @param coll The collection to copy
     * @param map Map implementation. It defines how elements would be compared. For example HashMap (by hashcode/equals),
     *            TreeMap (by compareTo or Comparator), IdentityHashMap (by identity). Specified map should be empty.
     * @throws NullPointerException if the collection is null
     */
    public IndexedTreeListSet(final Collection<? extends E> coll, final Map map) {
        this.nodeMap = map;
        this.supportAddSetInIterator = SUPPORT_ADD_SET_IN_ITERATOR;
        for (E e : coll) {
            add(e);
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Searches for the index of an object in the list.
     *
     * @param object the object to search
     * @return the index of the object, -1 if not found
     */
    @Override
    public int indexOf(final Object object) {
        AVLNode node = nodeMap.get(object);
        if (node == null) {
            return -1;
        }
        return node.getPosition();
    }

    /**
     * Searches for the last index of an object in the list.
     *
     * @param object  the object to search
     * @return the index of the object, -1 if not found
     */
    @Override
    public int lastIndexOf(final Object object) {
        return indexOf(object);
    }

    /**
     * Searches for the presence of an object in the list.
     *
     * @param object the object to check
     * @return true if the object is found
     */
    @Override
    public boolean contains(final Object object) {
        return nodeMap.containsKey(object);
    }

    /**
     * Sets the element at the specified index.
     * If specified value already exist in Set, it will be removed at old position.
     * E.g. if TreeListSet contains ["A", "B", "C"] and set(2, "A") is invoked, then result will be ["B", "A"].
     *
     * @param index the index to set
     * @param obj the object to store at the specified index
     * @return
     */
    @Override
    public E set(int index, final E obj) {
        final int pos = indexOf(obj);
        if (pos >= 0 && pos != index) {
            remove(pos);
            if (pos < index) {
                index--;
            }
        }
        return super.set(index, obj);
    }

    /**
     * Clears the list, removing all entries.
     */
    @Override
    public void clear() {
        super.clear();
        nodeMap.clear();
    }

    /**
     * Check if set does not contains an object.
     */
    @Override
    protected boolean canAdd(E e) {
        if (e == null) {
            throw new NullPointerException("Null elements are not allowed");
        }
        return !nodeMap.containsKey(e);
    }

    /**
     * Add node to nodeMap.
     */
    @Override
    protected void addNode(AVLNode node) {
        nodeMap.put(node.getValue(), node);
    }

    /**
     * Remove node from nodeMap.
     */
    @Override
    protected void removeNode(AVLNode node) {
        nodeMap.remove(node.getValue());
    }

}
