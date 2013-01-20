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
package org.apache.commons.collections.trie;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.Trie.Cursor.Decision;

/**
 * This class implements the base PATRICIA algorithm and everything that
 * is related to the {@link Map} interface.
 * 
 * @since 4.0
 * @version $Id$
 */
abstract class PatriciaTrieBase<K, V> extends AbstractTrie<K, V> {
    
    private static final long serialVersionUID = 5155253417231339498L;

    /**
     * The root node of the {@link Trie}. 
     */
    final TrieEntry<K, V> root = new TrieEntry<K, V>(null, null, -1);
    
    /**
     * Each of these fields are initialized to contain an instance of the
     * appropriate view the first time this view is requested. The views are
     * stateless, so there's no reason to create more than one of each.
     */
    private transient volatile Set<K> keySet;
    private transient volatile Collection<V> values;
    private transient volatile Set<Map.Entry<K,V>> entrySet;
    
    /**
     * The current size of the {@link Trie}
     */
    private int size = 0;
    
    /**
     * The number of times this {@link Trie} has been modified.
     * It's used to detect concurrent modifications and fail-fast
     * the {@link Iterator}s.
     */
    transient int modCount = 0;
    
    /** 
     * {@inheritDoc}
     */
    public PatriciaTrieBase(final KeyAnalyzer<? super K> keyAnalyzer) {
        super(keyAnalyzer);
    }
    
    /**
     * Constructs a new {@link Trie} using the given {@link KeyAnalyzer} 
     * and initializes the {@link Trie} with the values from the 
     * provided {@link Map}.
     */
    public PatriciaTrieBase(final KeyAnalyzer<? super K> keyAnalyzer, 
            final Map<? extends K, ? extends V> m) {
        super(keyAnalyzer);
        
        if (m == null) {
            throw new NullPointerException("m");
        }
        
        putAll(m);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        root.key = null;
        root.bitIndex = -1;
        root.value = null;
        
        root.parent = null;
        root.left = root;
        root.right = null;
        root.predecessor = root;
        
        size = 0;
        incrementModCount();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }
   
    /**
     * A helper method to increment the {@link Trie} size
     * and the modification counter.
     */
    void incrementSize() {
        size++;
        incrementModCount();
    }
    
    /**
     * A helper method to decrement the {@link Trie} size
     * and increment the modification counter.
     */
    void decrementSize() {
        size--;
        incrementModCount();
    }
    
    /**
     * A helper method to increment the modification counter.
     */
    private void incrementModCount() {
        ++modCount;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public V put(final K key, final V value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        
        final int lengthInBits = lengthInBits(key);
        
        // The only place to store a key with a length
        // of zero bits is the root node
        if (lengthInBits == 0) {
            if (root.isEmpty()) {
                incrementSize();
            } else {
                incrementModCount();
            }
            return root.setKeyValue(key, value);
        }
        
        final TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            if (found.isEmpty()) { // <- must be the root
                incrementSize();
            } else {
                incrementModCount();
            }
            return found.setKeyValue(key, value);
        }
        
        final int bitIndex = bitIndex(key, found.key);
        if (!AbstractKeyAnalyzer.isOutOfBoundsIndex(bitIndex)) {
            if (AbstractKeyAnalyzer.isValidBitIndex(bitIndex)) { // in 99.999...9% the case
                /* NEW KEY+VALUE TUPLE */
                final TrieEntry<K, V> t = new TrieEntry<K, V>(key, value, bitIndex);
                addEntry(t, lengthInBits);
                incrementSize();
                return null;
            } else if (AbstractKeyAnalyzer.isNullBitKey(bitIndex)) {
                // A bits of the Key are zero. The only place to
                // store such a Key is the root Node!
                
                /* NULL BIT KEY */
                if (root.isEmpty()) {
                    incrementSize();
                } else {
                    incrementModCount();
                }
                return root.setKeyValue(key, value);
                
            } else if (AbstractKeyAnalyzer.isEqualBitKey(bitIndex)) {
                // This is a very special and rare case.
                
                /* REPLACE OLD KEY+VALUE */
                if (found != root) {
                    incrementModCount();
                    return found.setKeyValue(key, value);
                }
            }
        }
        
        throw new IndexOutOfBoundsException("Failed to put: " + key + " -> " + value + ", " + bitIndex);
    }
    
    /**
     * Adds the given {@link TrieEntry} to the {@link Trie}
     */
    TrieEntry<K, V> addEntry(final TrieEntry<K, V> entry, final int lengthInBits) {
        TrieEntry<K, V> current = root.left;
        TrieEntry<K, V> path = root;
        while(true) {
            if (current.bitIndex >= entry.bitIndex 
                    || current.bitIndex <= path.bitIndex) {
                entry.predecessor = entry;
                
                if (!isBitSet(entry.key, entry.bitIndex, lengthInBits)) {
                    entry.left = entry;
                    entry.right = current;
                } else {
                    entry.left = current;
                    entry.right = entry;
                }
               
                entry.parent = path;
                if (current.bitIndex >= entry.bitIndex) {
                    current.parent = entry;
                }
                
                // if we inserted an uplink, set the predecessor on it
                if (current.bitIndex <= path.bitIndex) {
                    current.predecessor = entry;
                }
         
                if (path == root || !isBitSet(entry.key, path.bitIndex, lengthInBits)) {
                    path.left = entry;
                } else {
                    path.right = entry;
                }
                
                return entry;
            }
                
            path = current;
            
            if (!isBitSet(entry.key, current.bitIndex, lengthInBits)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public V get(final Object k) {
        final TrieEntry<K, V> entry = getEntry(k);
        return entry != null ? entry.getValue() : null;
    }

    /**
     * Returns the entry associated with the specified key in the
     * PatriciaTrieBase.  Returns null if the map contains no mapping
     * for this key.
     * 
     * This may throw ClassCastException if the object is not of type K.
     */
    TrieEntry<K,V> getEntry(final Object k) {
        final K key = castKey(k);
        if (key == null) {
            return null;
        }
        
        final int lengthInBits = lengthInBits(key);
        final TrieEntry<K,V> entry = getNearestEntryForKey(key, lengthInBits);
        return !entry.isEmpty() && compareKeys(key, entry.key) ? entry : null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Map.Entry<K, V> select(final K key) {
        final int lengthInBits = lengthInBits(key);
        final Reference<Map.Entry<K, V>> reference 
            = new Reference<Map.Entry<K,V>>();
        if (!selectR(root.left, -1, key, lengthInBits, reference)) {
            return reference.get();
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Map.Entry<K,V> select(final K key, final Cursor<? super K, ? super V> cursor) {
        final int lengthInBits = lengthInBits(key);
        final Reference<Map.Entry<K, V>> reference 
            = new Reference<Map.Entry<K,V>>();
        selectR(root.left, -1, key, lengthInBits, cursor, reference);
        return reference.get();
    }

    /**
     * This is equivalent to the other {@link #selectR(TrieEntry, int, 
     * Object, int, Cursor, Reference)} method but without its overhead 
     * because we're selecting only one best matching Entry from the 
     * {@link Trie}.
     */
    private boolean selectR(final TrieEntry<K, V> h, final int bitIndex, 
            final K key, final int lengthInBits, 
            final Reference<Map.Entry<K, V>> reference) {
        
        if (h.bitIndex <= bitIndex) {
            // If we hit the root Node and it is empty
            // we have to look for an alternative best
            // matching node.
            if (!h.isEmpty()) {
                reference.set(h);
                return false;
            }
            return true;
        }

        if (!isBitSet(key, h.bitIndex, lengthInBits)) {
            if (selectR(h.left, h.bitIndex, key, lengthInBits, reference)) {
                return selectR(h.right, h.bitIndex, key, lengthInBits, reference);
            }
        } else {
            if (selectR(h.right, h.bitIndex, key, lengthInBits, reference)) {
                return selectR(h.left, h.bitIndex, key, lengthInBits, reference);
            }
        }
        return false;
    }
    
    /**
     * 
     */
    private boolean selectR(final TrieEntry<K,V> h, final int bitIndex, 
            final K key, 
            final int lengthInBits,
            final Cursor<? super K, ? super V> cursor,
            final Reference<Map.Entry<K, V>> reference) {

        if (h.bitIndex <= bitIndex) {
            if (!h.isEmpty()) {
                final Decision decision = cursor.select(h);
                switch(decision) {
                    case REMOVE:
                        throw new UnsupportedOperationException("Cannot remove during select");
                    case EXIT:
                        reference.set(h);
                        return false; // exit
                    case REMOVE_AND_EXIT:
                        final TrieEntry<K, V> entry = new TrieEntry<K, V>(
                                h.getKey(), h.getValue(), -1);
                        reference.set(entry);
                        removeEntry(h);
                        return false;
                    case CONTINUE:
                        // fall through.
                    default:
                        break;
                }
            }
            return true; // continue
        }

        if (!isBitSet(key, h.bitIndex, lengthInBits)) {
            if (selectR(h.left, h.bitIndex, key, lengthInBits, cursor, reference)) {
                return selectR(h.right, h.bitIndex, key, lengthInBits, cursor, reference);
            }
        } else {
            if (selectR(h.right, h.bitIndex, key, lengthInBits, cursor, reference)) {
                return selectR(h.left, h.bitIndex, key, lengthInBits, cursor, reference);
            }
        }
        
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Map.Entry<K, V> traverse(final Cursor<? super K, ? super V> cursor) {
        TrieEntry<K, V> entry = nextEntry(null);
        while (entry != null) {
            final TrieEntry<K, V> current = entry;
            
            final Decision decision = cursor.select(current);
            entry = nextEntry(current);
            
            switch(decision) {
                case EXIT:
                    return current;
                case REMOVE:
                    removeEntry(current);
                    break; // out of switch, stay in while loop
                case REMOVE_AND_EXIT:
                    final Map.Entry<K, V> value = new TrieEntry<K, V>(
                            current.getKey(), current.getValue(), -1);
                    removeEntry(current);
                    return value;
                case CONTINUE: // do nothing.
                default:
                    break;
            }
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(final Object k) {
        if (k == null) {
            return false;
        }
        
        final K key = castKey(k);
        final int lengthInBits = lengthInBits(key);
        final TrieEntry<K, V> entry = getNearestEntryForKey(key, lengthInBits);
        return !entry.isEmpty() && compareKeys(key, entry.key);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new Values();
        }
        return values;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws ClassCastException if provided key is of an incompatible type 
     */
    @Override
    public V remove(final Object k) {
        if (k == null) {
            return null;
        }
        
        final K key = castKey(k);
        final int lengthInBits = lengthInBits(key);        
        TrieEntry<K, V> current = root.left;
        TrieEntry<K, V> path = root;
        while (true) {
            if (current.bitIndex <= path.bitIndex) {
                if (!current.isEmpty() && compareKeys(key, current.key)) {
                    return removeEntry(current);
                } else {
                    return null;
                }
            }
            
            path = current;
            
            if (!isBitSet(key, current.bitIndex, lengthInBits)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
    }
    
    /**
     * Returns the nearest entry for a given key.  This is useful
     * for finding knowing if a given key exists (and finding the value
     * for it), or for inserting the key.
     * 
     * The actual get implementation. This is very similar to
     * selectR but with the exception that it might return the
     * root Entry even if it's empty.
     */
    TrieEntry<K, V> getNearestEntryForKey(final K key, final int lengthInBits) {
        TrieEntry<K, V> current = root.left;
        TrieEntry<K, V> path = root;
        while(true) {
            if (current.bitIndex <= path.bitIndex) {
                return current;
            }
            
            path = current;
            if (!isBitSet(key, current.bitIndex, lengthInBits)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
    }
    
    /**
     * Removes a single entry from the {@link Trie}.
     * 
     * If we found a Key (Entry h) then figure out if it's
     * an internal (hard to remove) or external Entry (easy 
     * to remove)
     */
    V removeEntry(final TrieEntry<K, V> h) {
        if (h != root) {
            if (h.isInternalNode()) {
                removeInternalEntry(h);
            } else {
                removeExternalEntry(h);
            }
        }
        
        decrementSize();
        return h.setKeyValue(null, null);
    }
    
    /**
     * Removes an external entry from the {@link Trie}.
     * 
     * If it's an external Entry then just remove it.
     * This is very easy and straight forward.
     */
    private void removeExternalEntry(final TrieEntry<K, V> h) {
        if (h == root) {
            throw new IllegalArgumentException("Cannot delete root Entry!");
        } else if (!h.isExternalNode()) {
            throw new IllegalArgumentException(h + " is not an external Entry!");
        } 
        
        final TrieEntry<K, V> parent = h.parent;
        final TrieEntry<K, V> child = h.left == h ? h.right : h.left;
        
        if (parent.left == h) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        
        // either the parent is changing, or the predecessor is changing.
        if (child.bitIndex > parent.bitIndex) {
            child.parent = parent;
        } else {
            child.predecessor = parent;
        }
        
    }
    
    /**
     * Removes an internal entry from the {@link Trie}.
     * 
     * If it's an internal Entry then "good luck" with understanding
     * this code. The Idea is essentially that Entry p takes Entry h's
     * place in the trie which requires some re-wiring.
     */
    private void removeInternalEntry(final TrieEntry<K, V> h) {
        if (h == root) {
            throw new IllegalArgumentException("Cannot delete root Entry!");
        } else if (!h.isInternalNode()) {
            throw new IllegalArgumentException(h + " is not an internal Entry!");
        } 
        
        final TrieEntry<K, V> p = h.predecessor;
        
        // Set P's bitIndex
        p.bitIndex = h.bitIndex;
        
        // Fix P's parent, predecessor and child Nodes
        {
            final TrieEntry<K, V> parent = p.parent;
            final TrieEntry<K, V> child = p.left == h ? p.right : p.left;
            
            // if it was looping to itself previously,
            // it will now be pointed from it's parent
            // (if we aren't removing it's parent --
            //  in that case, it remains looping to itself).
            // otherwise, it will continue to have the same
            // predecessor.
            if (p.predecessor == p && p.parent != h) {
                p.predecessor = p.parent;
            }
            
            if (parent.left == p) {
                parent.left = child;
            } else {
                parent.right = child;
            }
            
            if (child.bitIndex > parent.bitIndex) {
                child.parent = parent;
            }
        }
        
        // Fix H's parent and child Nodes
        {         
            // If H is a parent of its left and right child 
            // then change them to P
            if (h.left.parent == h) {
                h.left.parent = p;
            }
            
            if (h.right.parent == h) {
                h.right.parent = p;
            }
            
            // Change H's parent
            if (h.parent.left == h) {
                h.parent.left = p;
            } else {
                h.parent.right = p;
            }
        }
        
        // Copy the remaining fields from H to P
        //p.bitIndex = h.bitIndex;
        p.parent = h.parent;
        p.left = h.left;
        p.right = h.right;
        
        // Make sure that if h was pointing to any uplinks,
        // p now points to them.
        if (isValidUplink(p.left, p)) {
            p.left.predecessor = p;
        }
        
        if (isValidUplink(p.right, p)) {
            p.right.predecessor = p;
        }   
    }
    
    /**
     * Returns the entry lexicographically after the given entry.
     * If the given entry is null, returns the first node.
     */
    TrieEntry<K, V> nextEntry(final TrieEntry<K, V> node) {
        if (node == null) {
            return firstEntry();
        } else {
            return nextEntryImpl(node.predecessor, node, null);
        }
    }
    
    /**
     * Scans for the next node, starting at the specified point, and using 'previous'
     * as a hint that the last node we returned was 'previous' (so we know not to return
     * it again).  If 'tree' is non-null, this will limit the search to the given tree.
     * 
     * The basic premise is that each iteration can follow the following steps:
     * 
     * 1) Scan all the way to the left.
     *   a) If we already started from this node last time, proceed to Step 2.
     *   b) If a valid uplink is found, use it.
     *   c) If the result is an empty node (root not set), break the scan.
     *   d) If we already returned the left node, break the scan.
     *   
     * 2) Check the right.
     *   a) If we already returned the right node, proceed to Step 3.
     *   b) If it is a valid uplink, use it.
     *   c) Do Step 1 from the right node.
     *   
     * 3) Back up through the parents until we encounter find a parent
     *    that we're not the right child of.
     *    
     * 4) If there's no right child of that parent, the iteration is finished.
     *    Otherwise continue to Step 5.
     * 
     * 5) Check to see if the right child is a valid uplink.
     *    a) If we already returned that child, proceed to Step 6.
     *       Otherwise, use it.
     *    
     * 6) If the right child of the parent is the parent itself, we've
     *    already found & returned the end of the Trie, so exit.
     *    
     * 7) Do Step 1 on the parent's right child.
     */
    TrieEntry<K, V> nextEntryImpl(final TrieEntry<K, V> start, 
            final TrieEntry<K, V> previous, final TrieEntry<K, V> tree) {
        
        TrieEntry<K, V> current = start;

        // Only look at the left if this was a recursive or
        // the first check, otherwise we know we've already looked
        // at the left.
        if (previous == null || start != previous.predecessor) {
            while (!current.left.isEmpty()) {
                // stop traversing if we've already
                // returned the left of this node.
                if (previous == current.left) {
                    break;
                }
                
                if (isValidUplink(current.left, current)) {
                    return current.left;
                }
                
                current = current.left;
            }
        }
        
        // If there's no data at all, exit.
        if (current.isEmpty()) {
            return null;
        }
        
        // If we've already returned the left,
        // and the immediate right is null,
        // there's only one entry in the Trie
        // which is stored at the root.
        //
        //  / ("")   <-- root
        //  \_/  \
        //       null <-- 'current'
        //
        if (current.right == null) {
            return null;
        }
        
        // If nothing valid on the left, try the right.
        if (previous != current.right) {
            // See if it immediately is valid.
            if (isValidUplink(current.right, current)) {
                return current.right;
            }
            
            // Must search on the right's side if it wasn't initially valid.
            return nextEntryImpl(current.right, previous, tree);
        }
        
        // Neither left nor right are valid, find the first parent
        // whose child did not come from the right & traverse it.
        while (current == current.parent.right) {
            // If we're going to traverse to above the subtree, stop.
            if (current == tree) {
                return null;
            }
            
            current = current.parent;
        }

        // If we're on the top of the subtree, we can't go any higher.
        if (current == tree) {
            return null;
        }
        
        // If there's no right, the parent must be root, so we're done.
        if (current.parent.right == null) {
            return null;
        }
        
        // If the parent's right points to itself, we've found one.
        if (previous != current.parent.right 
                && isValidUplink(current.parent.right, current.parent)) {
            return current.parent.right;
        }
        
        // If the parent's right is itself, there can't be any more nodes.
        if (current.parent.right == current.parent) {
            return null;
        }
        
        // We need to traverse down the parent's right's path.
        return nextEntryImpl(current.parent.right, previous, tree);
    }
    
    /**
     * Returns the first entry the {@link Trie} is storing.
     * 
     * This is implemented by going always to the left until
     * we encounter a valid uplink. That uplink is the first key.
     */
    TrieEntry<K, V> firstEntry() {
        // if Trie is empty, no first node.
        if (isEmpty()) {
            return null;
        }
        
        return followLeft(root);
    }
    
    /** 
     * Goes left through the tree until it finds a valid node. 
     */
    TrieEntry<K, V> followLeft(TrieEntry<K, V> node) {
        while(true) {
            TrieEntry<K, V> child = node.left;
            // if we hit root and it didn't have a node, go right instead.
            if (child.isEmpty()) {
                child = node.right;
            }
            
            if (child.bitIndex <= node.bitIndex) {
                return child;
            }
            
            node = child;
        }
    }
    
    /** 
     * Returns true if 'next' is a valid uplink coming from 'from'. 
     */
    static boolean isValidUplink(final TrieEntry<?, ?> next, final TrieEntry<?, ?> from) { 
        return next != null && next.bitIndex <= from.bitIndex && !next.isEmpty();
    }
    
    /**
     * A {@link Reference} allows us to return something through a Method's 
     * argument list. An alternative would be to an Array with a length of 
     * one (1) but that leads to compiler warnings. Computationally and memory
     * wise there's no difference (except for the need to load the 
     * {@link Reference} Class but that happens only once).
     */
    private static class Reference<E> {
        
        private E item;
        
        public void set(final E item) {
            this.item = item;
        }
        
        public E get() {
            return item;
        }
    }
    
    /**
     *  A {@link Trie} is a set of {@link TrieEntry} nodes
     */
    static class TrieEntry<K,V> extends BasicEntry<K, V> {
        
        private static final long serialVersionUID = 4596023148184140013L;
        
        /** The index this entry is comparing. */
        protected int bitIndex;
        
        /** The parent of this entry. */
        protected TrieEntry<K,V> parent;
        
        /** The left child of this entry. */
        protected TrieEntry<K,V> left;
        
        /** The right child of this entry. */
        protected TrieEntry<K,V> right;
        
        /** The entry who uplinks to this entry. */ 
        protected TrieEntry<K,V> predecessor;
        
        public TrieEntry(final K key, final V value, final int bitIndex) {
            super(key, value);
            
            this.bitIndex = bitIndex;
            
            this.parent = null;
            this.left = this;
            this.right = null;
            this.predecessor = this;
        }
        
        /**
         * Whether or not the entry is storing a key.
         * Only the root can potentially be empty, all other
         * nodes must have a key.
         */
        public boolean isEmpty() {
            return key == null;
        }
        
        /** 
         * Neither the left nor right child is a loopback 
         */
        public boolean isInternalNode() {
            return left != this && right != this;
        }
        
        /** 
         * Either the left or right child is a loopback 
         */
        public boolean isExternalNode() {
            return !isInternalNode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            
            if (bitIndex == -1) {
                buffer.append("RootEntry(");
            } else {
                buffer.append("Entry(");
            }
            
            buffer.append("key=").append(getKey()).append(" [").append(bitIndex).append("], ");
            buffer.append("value=").append(getValue()).append(", ");
            //buffer.append("bitIndex=").append(bitIndex).append(", ");
            
            if (parent != null) {
                if (parent.bitIndex == -1) {
                    buffer.append("parent=").append("ROOT");
                } else {
                    buffer.append("parent=").append(parent.getKey()).append(" [").append(parent.bitIndex).append("]");
                }
            } else {
                buffer.append("parent=").append("null");
            }
            buffer.append(", ");
            
            if (left != null) {
                if (left.bitIndex == -1) {
                    buffer.append("left=").append("ROOT");
                } else {
                    buffer.append("left=").append(left.getKey()).append(" [").append(left.bitIndex).append("]");
                }
            } else {
                buffer.append("left=").append("null");
            }
            buffer.append(", ");
            
            if (right != null) {
                if (right.bitIndex == -1) {
                    buffer.append("right=").append("ROOT");
                } else {
                    buffer.append("right=").append(right.getKey()).append(" [").append(right.bitIndex).append("]");
                }
            } else {
                buffer.append("right=").append("null");
            }
            buffer.append(", ");
            
            if (predecessor != null) {
                if(predecessor.bitIndex == -1) {
                    buffer.append("predecessor=").append("ROOT");
                } else {
                    buffer.append("predecessor=").append(predecessor.getKey()).append(" [").
                           append(predecessor.bitIndex).append("]");
                }
            }
            
            buffer.append(")");
            return buffer.toString();
        }
    }
    

    /**
     * This is a entry set view of the {@link Trie} as returned 
     * by {@link Map#entrySet()}
     */
    private class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            
            final TrieEntry<K,V> candidate = getEntry(((Map.Entry<?, ?>)o).getKey());
            return candidate != null && candidate.equals(o);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(final Object o) {
            final int size = size();
            PatriciaTrieBase.this.remove(o);
            return size != size();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return PatriciaTrieBase.this.size();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            PatriciaTrieBase.this.clear();
        }
        
        /**
         * An {@link Iterator} that returns {@link Entry} Objects
         */
        private class EntryIterator extends TrieIterator<Map.Entry<K,V>> {
            public Map.Entry<K,V> next() {
                return nextEntry();
            }
        }
    }
    
    /**
     * This is a key set view of the {@link Trie} as returned 
     * by {@link Map#keySet()}
     */
    private class KeySet extends AbstractSet<K> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return PatriciaTrieBase.this.size();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(final Object o) {
            return containsKey(o);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(final Object o) {
            final int size = size();
            PatriciaTrieBase.this.remove(o);
            return size != size();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            PatriciaTrieBase.this.clear();
        }
        
        /**
         * An {@link Iterator} that returns Key Objects
         */
        private class KeyIterator extends TrieIterator<K> {
            public K next() {
                return nextEntry().getKey();
            }
        }
    }
    
    /**
     * This is a value view of the {@link Trie} as returned 
     * by {@link Map#values()}
     */
    private class Values extends AbstractCollection<V> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return PatriciaTrieBase.this.size();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            PatriciaTrieBase.this.clear();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(final Object o) {
            for (final Iterator<V> it = iterator(); it.hasNext(); ) {
                final V value = it.next();
                if (compare(value, o)) {
                    it.remove();
                    return true;
                }
            }
            return false;
        }
        
        /**
         * An {@link Iterator} that returns Value Objects
         */
        private class ValueIterator extends TrieIterator<V> {
            public V next() {
                return nextEntry().getValue();
            }
        }
    }
    
    /** 
     * An iterator for the entries. 
     */
    abstract class TrieIterator<E> implements Iterator<E> {
        
        /**
         * For fast-fail
         */
        protected int expectedModCount = PatriciaTrieBase.this.modCount;
        
        protected TrieEntry<K, V> next; // the next node to return
        protected TrieEntry<K, V> current; // the current entry we're on
        
        /**
         * Starts iteration from the root
         */
        protected TrieIterator() {
            next = PatriciaTrieBase.this.nextEntry(null);
        }
        
        /**
         * Starts iteration at the given entry
         */
        protected TrieIterator(final TrieEntry<K, V> firstEntry) {
            next = firstEntry;
        }
        
        /**
         * Returns the next {@link TrieEntry}
         */
        protected TrieEntry<K,V> nextEntry() { 
            if (expectedModCount != PatriciaTrieBase.this.modCount) {
                throw new ConcurrentModificationException();
            }
            
            final TrieEntry<K,V> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            
            next = findNext(e);
            current = e;
            return e;
        }
        
        /**
         * @see PatriciaTrie#nextEntry(TrieEntry)
         */
        protected TrieEntry<K, V> findNext(final TrieEntry<K, V> prior) {
            return PatriciaTrieBase.this.nextEntry(prior);
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            return next != null;
        }
        
        /**
         * {@inheritDoc}
         */
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            
            if (expectedModCount != PatriciaTrieBase.this.modCount) {
                throw new ConcurrentModificationException();
            }
            
            final TrieEntry<K, V> node = current;
            current = null;
            PatriciaTrieBase.this.removeEntry(node);
            
            expectedModCount = PatriciaTrieBase.this.modCount;
        }
    }
}
