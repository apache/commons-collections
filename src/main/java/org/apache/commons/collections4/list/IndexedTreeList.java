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

import java.util.*;
import java.util.function.Function;

/**
 * <p>
 * As a <code>List</code> this data structure stores order of elements and
 * provides access by index. It's is optimised for fast insertions, removals
 * and searching by any index or object in the list.
 * </p>
 * <p>
 * IndexedTreeList can be suitable for tasks which requires fast modification in the
 * middle of a list and provides fast contains and indexOf.
 * </p>
 * <p>
 * Get by index is O(log n).
 * Insert (head, tail, middle) and remove(by index or by value) are O(log n)
 * in most cases but can be up to O((log n) ^ 2) in cases when List contains big
 * amount of elements equals to each other. Actual complexity is
 * O((log n) * (1 + log m)) where m is amount of elements equal to inserted/removed.
 * indexOf is O(log n).
 * Contains is O(1) or O(log n) depending on Map implementation.
 * </p>
 * <p>
 * Internally it uses Map (HashMap by default) and AVL tree.
 * HashMap can be replaced to TreeMap, this will slightly reduce overall performance
 * but will eliminate problems with hash collisions and hash table resizing.
 * Using TreeMap with custom Comparator will provide indexOf by custom criteria.
 * Using IdentityHashMap will provide indexOf by object's identity.
 * </p>
 * <p>
 * Objects equality is checked by Map, so objects should be immutable for Map
 * consistency.
 * </p>
 * <p>
 * Code is based on apache common collections <code>TreeList</code>.
 * </p>
 * Comparing to <code>TreeList</code> this data structure:
 * <ul>
 * <li>Has slightly slower insertion/removing operations, O(log n) in most cases, O((log n) ^ 2) in
 * worst cases (if TreeMap is used)</li>
 * <li>Requires more memory, however it's still O(n)</li>
 * <li>Has greatly improved contains and indexOf operations, O(log n) while TreeList has O(n)</li>
 * </ul>
 *
 * <p>
 * As this implementation is slightly slower and require more memory it's recommended to use
 * <code>TreeList</code> in cases when no searching is required or <code>IndexedTreeListSet</code>
 * in cases where unique elements should be stored.
 * </p>
 *
 * @author Aleksandr Maksymenko
 */
public class IndexedTreeList<E> extends AbstractIndexedTreeList<E> {

    private final Comparator<AVLNode> NODE_COMPARATOR = Comparator.comparingInt(AVLNode::getPosition);
    private final Function<E, TreeSet<AVLNode>> NEW_NODE_TREE_SET = k -> new TreeSet(NODE_COMPARATOR);

    /** Map from element to it's node or nodes */
    protected final Map<E, TreeSet<AVLNode>> nodeMap;

    //-----------------------------------------------------------------------
    /**
     * Constructs a new empty list.
     */
    public IndexedTreeList() {
        this(new HashMap<>());
    }

    /**
     * Constructs a new empty list.
     * @param map Map implementation. It defines how elements would be compared. For example HashMap (by hashcode/equals),
     *            TreeMap (by compareTo or Comparator), IdentityHashMap (by identity). Specified map should be empty.
     */
    public IndexedTreeList(final Map map) {
        this.nodeMap = map;
    }

    /**
     * Constructs a new list that copies the specified collection.
     *
     * @param coll The collection to copy
     * @throws NullPointerException if the collection is null
     */
    public IndexedTreeList(final Collection<? extends E> coll) {
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
    public IndexedTreeList(final Collection<? extends E> coll, final Map map) {
        this.nodeMap = map;
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
        TreeSet<AVLNode> nodes = nodeMap.get(object);
        if (nodes == null || nodes.isEmpty()) {
            return -1;
        }
        return nodes.first().getPosition();
    }

    /**
     * Searches for the last index of an object in the list.
     *
     * @param object the object to search
     * @return the index of the object, -1 if not found
     */
    @Override
    public int lastIndexOf(final Object object) {
        TreeSet<AVLNode> nodes = nodeMap.get(object);
        if (nodes == null || nodes.isEmpty()) {
            return -1;
        }
        return nodes.last().getPosition();
    }

    /**
     * Searches for all indexes of an objects in the list equals to specified object.
     *
     * @param object the object to search
     * @return array of indexes of the objects
     */
    public int[] indexes(final Object object) {
        TreeSet<AVLNode> nodes = nodeMap.get(object);
        if (nodes == null || nodes.isEmpty()) {
            return new int[0];
        }
        int[] indexes = new int[nodes.size()];
        int i = 0;
        for (AVLNode node : nodes) {
            indexes[i++] = node.getPosition();
        }
        return indexes;
    }

    /**
     * Get amount of objects in the list equals to specified object.
     *
     * @param object the object to search
     * @return amount of objects
     */
    public int count(final Object object) {
        TreeSet<AVLNode> nodes = nodeMap.get(object);
        if (nodes == null || nodes.isEmpty()) {
            return 0;
        }
        return nodes.size();
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
     * Clears the list, removing all entries.
     */
    @Override
    public void clear() {
        super.clear();
        nodeMap.clear();
    }

    /**
     * Get unordered Set of unique values.
     *
     * @return unordered Set of unique values
     */
    public Set<E> uniqueValues() {
        return nodeMap.keySet();
    }

    /**
     * Check if set does not contains an object.
     *
     * @param e element to check if it can be added to collection
     * @return true if specified element can be added to collection
     */
    @Override
    protected boolean canAdd(E e) {
        if (e == null) {
            throw new NullPointerException("Null elements are not allowed");
        }
        return true;
    }

    /**
     * Add node to nodeMap.
     */
    @Override
    protected void addNode(AVLNode node) {
        nodeMap.computeIfAbsent(node.getValue(), NEW_NODE_TREE_SET).add(node);
    }

    /**
     * Remove node from nodeMap.
     */
    @Override
    protected void removeNode(AVLNode node) {
        TreeSet<AVLNode> nodes = nodeMap.remove(node.getValue());
        if (nodes == null) {
            return;
        }
        nodes.remove(node);
        if (!nodes.isEmpty()) {
            nodeMap.put(nodes.first().getValue(), nodes);
        }
    }

}
