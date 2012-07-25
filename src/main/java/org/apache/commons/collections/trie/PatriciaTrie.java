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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections.Trie;

/**
 * <h3>PATRICIA {@link Trie}</h3>
 *  
 * <i>Practical Algorithm to Retrieve Information Coded in Alphanumeric</i>
 * 
 * <p>A PATRICIA {@link Trie} is a compressed {@link Trie}. Instead of storing 
 * all data at the edges of the {@link Trie} (and having empty internal nodes), 
 * PATRICIA stores data in every node. This allows for very efficient traversal, 
 * insert, delete, predecessor, successor, prefix, range, and {@link #select(Object)} 
 * operations. All operations are performed at worst in O(K) time, where K 
 * is the number of bits in the largest item in the tree. In practice, 
 * operations actually take O(A(K)) time, where A(K) is the average number of 
 * bits of all items in the tree.
 * 
 * <p>Most importantly, PATRICIA requires very few comparisons to keys while
 * doing any operation. While performing a lookup, each comparison (at most 
 * K of them, described above) will perform a single bit comparison against 
 * the given key, instead of comparing the entire key to another key.
 * 
 * <p>The {@link Trie} can return operations in lexicographical order using the 
 * {@link #traverse(Cursor)}, 'prefix', 'submap', or 'iterator' methods. The 
 * {@link Trie} can also scan for items that are 'bitwise' (using an XOR 
 * metric) by the 'select' method. Bitwise closeness is determined by the 
 * {@link KeyAnalyzer} returning true or false for a bit being set or not in 
 * a given key.
 * 
 * <p>This PATRICIA {@link Trie} supports both variable length & fixed length 
 * keys. Some methods, such as {@link #getPrefixedBy(Object)} are suited only 
 * to variable length keys, whereas {@link #getPrefixedByBits(Object, int)} is 
 * suited to fixed-size keys.
 * 
 * <p>Any methods here that take an {@link Object} argument may throw a 
 * {@link ClassCastException} if the method is expecting an instance of K 
 * and it isn't K.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Radix_tree">Radix Tree</a>
 * @see <a href="http://www.csse.monash.edu.au/~lloyd/tildeAlgDS/Tree/PATRICIA">PATRICIA</a>
 * @see <a href="http://www.imperialviolet.org/binary/critbit.pdf">Crit-Bit Tree</a>
 * @since 4.0
 * @version $Id$
 */
public class PatriciaTrie<K, V> extends PatriciaTrieBase<K, V> implements Trie<K, V> {
    
    private static final long serialVersionUID = 4446367780901817838L;

    public PatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer) {
        super(keyAnalyzer);
    }

    public PatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer,
            Map<? extends K, ? extends V> m) {
        super(keyAnalyzer, m);
    }
    
    /**
     * {@inheritDoc}
     */
    public Comparator<? super K> comparator() {
        return keyAnalyzer;
    }
    
    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedBy(K key) {
        return getPrefixedByBits(key, 0, lengthInBits(key));
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedBy(K key, int length) {
        return getPrefixedByBits(key, 0, length * bitsPerElement());
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedBy(K key, int offset, int length) {
        int bitsPerElement = bitsPerElement();
        return getPrefixedByBits(key, offset*bitsPerElement, length*bitsPerElement);
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> getPrefixedByBits(K key, int lengthInBits) {
        return getPrefixedByBits(key, 0, lengthInBits);
    }
    
    /**
     * {@inheritDoc}
     */
    public K firstKey() {
        return firstEntry().getKey();
    }

    /**
     * {@inheritDoc}
     */
    public K lastKey() {
        TrieEntry<K, V> entry = lastEntry();
        if (entry != null) {
            return entry.getKey();
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     * 
     * The view that this returns is optimized to have a very efficient
     * {@link Iterator}. The {@link SortedMap#firstEntry()}, 
     * {@link SortedMap#lastKey()} &amp; {@link Map#size()} methods must 
     * iterate over all possible values in order to determine the results. 
     * This information is cached until the PATRICIA {@link Trie} changes. 
     * All other methods (except {@link Iterator}) must compare the given 
     * key to the prefix to ensure that it is within the range of the view.  
     * The {@link Iterator}'s remove method must also relocate the subtree 
     * that contains the prefixes if the entry holding the subtree is 
     * removed or changes. Changing the subtree takes O(K) time.
     */
    public SortedMap<K, V> getPrefixedByBits(K key, int offsetInBits, int lengthInBits) {
        
        int offsetLength = offsetInBits + lengthInBits;
        if (offsetLength > lengthInBits(key)) {
            throw new IllegalArgumentException(offsetInBits + " + " 
                    + lengthInBits + " > " + lengthInBits(key));
        }
        
        if (offsetLength == 0) {
            return this;
        }
        
        return new PrefixRangeMap(key, offsetInBits, lengthInBits);
    }
    
    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> headMap(K toKey) {
        return new RangeEntryMap(null, toKey);
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return new RangeEntryMap(fromKey, toKey);
    }

    /**
     * {@inheritDoc}
     */
    public SortedMap<K, V> tailMap(K fromKey) {
        return new RangeEntryMap(fromKey, null);
    } 
    
    /**
     * Returns an entry strictly higher than the given key,
     * or null if no such entry exists.
     */
    TrieEntry<K,V> higherEntry(K key) {
        // TODO: Cleanup so that we don't actually have to add/remove from the
        //       tree.  (We do it here because there are other well-defined 
        //       functions to perform the search.)
        int lengthInBits = lengthInBits(key);
        
        if (lengthInBits == 0) {
            if (!root.isEmpty()) {
                // If data in root, and more after -- return it.
                if (size() > 1) {
                    return nextEntry(root);
                } else { // If no more after, no higher entry.
                    return null;
                }
            } else {
                // Root is empty & we want something after empty, return first.
                return firstEntry();
            }
        }
        
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return nextEntry(found);
        }
        
        int bitIndex = bitIndex(key, found.key);
        if (AbstractKeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize(); // must increment because remove will decrement
            TrieEntry<K, V> ceil = nextEntry(added);
            removeEntry(added);
            modCount -= 2; // we didn't really modify it.
            return ceil;
        } else if (AbstractKeyAnalyzer.isNullBitKey(bitIndex)) {
            if (!root.isEmpty()) {
                return firstEntry();
            } else if (size() > 1) {
                return nextEntry(firstEntry());
            } else {
                return null;
            }
        } else if (AbstractKeyAnalyzer.isEqualBitKey(bitIndex)) {
            return nextEntry(found);
        }

        // we should have exited above.
        throw new IllegalStateException("invalid lookup: " + key);
    }
    
    /**
     * Returns a key-value mapping associated with the least key greater
     * than or equal to the given key, or null if there is no such key.
     */
    TrieEntry<K,V> ceilingEntry(K key) {
        // Basically:
        // Follow the steps of adding an entry, but instead...
        //
        // - If we ever encounter a situation where we found an equal
        //   key, we return it immediately.
        //
        // - If we hit an empty root, return the first iterable item.
        //
        // - If we have to add a new item, we temporarily add it,
        //   find the successor to it, then remove the added item.
        //
        // These steps ensure that the returned value is either the
        // entry for the key itself, or the first entry directly after
        // the key.
        
        // TODO: Cleanup so that we don't actually have to add/remove from the
        //       tree.  (We do it here because there are other well-defined 
        //       functions to perform the search.)
        int lengthInBits = lengthInBits(key);
        
        if (lengthInBits == 0) {
            if (!root.isEmpty()) {
                return root;
            } else {
                return firstEntry();
            }
        }
        
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return found;
        }
        
        int bitIndex = bitIndex(key, found.key);
        if (AbstractKeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize(); // must increment because remove will decrement
            TrieEntry<K, V> ceil = nextEntry(added);
            removeEntry(added);
            modCount -= 2; // we didn't really modify it.
            return ceil;
        } else if (AbstractKeyAnalyzer.isNullBitKey(bitIndex)) {
            if (!root.isEmpty()) {
                return root;
            } else {
                return firstEntry();
            }
        } else if (AbstractKeyAnalyzer.isEqualBitKey(bitIndex)) {
            return found;
        }

        // we should have exited above.
        throw new IllegalStateException("invalid lookup: " + key);
    }
    
    /**
     * Returns a key-value mapping associated with the greatest key
     * strictly less than the given key, or null if there is no such key.
     */
    TrieEntry<K,V> lowerEntry(K key) {
        // Basically:
        // Follow the steps of adding an entry, but instead...
        //
        // - If we ever encounter a situation where we found an equal
        //   key, we return it's previousEntry immediately.
        //
        // - If we hit root (empty or not), return null.
        //
        // - If we have to add a new item, we temporarily add it,
        //   find the previousEntry to it, then remove the added item.
        //
        // These steps ensure that the returned value is always just before
        // the key or null (if there was nothing before it).
        
        // TODO: Cleanup so that we don't actually have to add/remove from the
        //       tree.  (We do it here because there are other well-defined 
        //       functions to perform the search.)
        int lengthInBits = lengthInBits(key);
        
        if (lengthInBits == 0) {
            return null; // there can never be anything before root.
        }
        
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return previousEntry(found);
        }
        
        int bitIndex = bitIndex(key, found.key);
        if (AbstractKeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize(); // must increment because remove will decrement
            TrieEntry<K, V> prior = previousEntry(added);
            removeEntry(added);
            modCount -= 2; // we didn't really modify it.
            return prior;
        } else if (AbstractKeyAnalyzer.isNullBitKey(bitIndex)) {
            return null;
        } else if (AbstractKeyAnalyzer.isEqualBitKey(bitIndex)) {
            return previousEntry(found);
        }

        // we should have exited above.
        throw new IllegalStateException("invalid lookup: " + key);
    }
    
    /**
     * Returns a key-value mapping associated with the greatest key
     * less than or equal to the given key, or null if there is no such key.
     */
    TrieEntry<K,V> floorEntry(K key) {        
        // TODO: Cleanup so that we don't actually have to add/remove from the
        //       tree.  (We do it here because there are other well-defined 
        //       functions to perform the search.)
        int lengthInBits = lengthInBits(key);
        
        if (lengthInBits == 0) {
            if (!root.isEmpty()) {
                return root;
            } else {
                return null;
            }
        }
        
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return found;
        }
        
        int bitIndex = bitIndex(key, found.key);
        if (AbstractKeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize(); // must increment because remove will decrement
            TrieEntry<K, V> floor = previousEntry(added);
            removeEntry(added);
            modCount -= 2; // we didn't really modify it.
            return floor;
        } else if (AbstractKeyAnalyzer.isNullBitKey(bitIndex)) {
            if (!root.isEmpty()) {
                return root;
            } else {
                return null;
            }
        } else if (AbstractKeyAnalyzer.isEqualBitKey(bitIndex)) {
            return found;
        }

        // we should have exited above.
        throw new IllegalStateException("invalid lookup: " + key);
    }
    
    /**
     * Finds the subtree that contains the prefix.
     * 
     * This is very similar to getR but with the difference that
     * we stop the lookup if h.bitIndex > lengthInBits.
     */
    TrieEntry<K, V> subtree(K prefix, int offsetInBits, int lengthInBits) {
        TrieEntry<K, V> current = root.left;
        TrieEntry<K, V> path = root;
        while(true) {
            if (current.bitIndex <= path.bitIndex 
                    || lengthInBits < current.bitIndex) {
                break;
            }
            
            path = current;
            if (!isBitSet(prefix, offsetInBits + current.bitIndex, 
                    offsetInBits + lengthInBits)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }        

        // Make sure the entry is valid for a subtree.
        TrieEntry<K, V> entry = current.isEmpty() ? path : current;
        
        // If entry is root, it can't be empty.
        if (entry.isEmpty()) {
            return null;
        }
        
        int endIndexInBits = offsetInBits + lengthInBits;
        
        // if root && length of root is less than length of lookup,
        // there's nothing.
        // (this prevents returning the whole subtree if root has an empty
        //  string and we want to lookup things with "\0")
        if (entry == root && lengthInBits(entry.getKey()) < endIndexInBits) {
            return null;
        }
        
        // Found key's length-th bit differs from our key
        // which means it cannot be the prefix...
        if (isBitSet(prefix, endIndexInBits, endIndexInBits) 
                != isBitSet(entry.key, lengthInBits, lengthInBits(entry.key))) {
            return null;
        }
        
        // ... or there are less than 'length' equal bits
        int bitIndex = keyAnalyzer.bitIndex(prefix, offsetInBits, 
                lengthInBits, entry.key, 0, lengthInBits(entry.getKey()));
        
        if (bitIndex >= 0 && bitIndex < lengthInBits) {
            return null;
        }
        
        return entry;
    }
    
    /**
     * Returns the last entry the {@link Trie} is storing.
     * 
     * <p>This is implemented by going always to the right until
     * we encounter a valid uplink. That uplink is the last key.
     */
    TrieEntry<K, V> lastEntry() {
        return followRight(root.left);
    }
    
    /**
     * Traverses down the right path until it finds an uplink.
     */
    TrieEntry<K, V> followRight(TrieEntry<K, V> node) {
        // if Trie is empty, no last entry.
        if (node.right == null) {
            return null;
        }
        
        // Go as far right as possible, until we encounter an uplink.
        while (node.right.bitIndex > node.bitIndex) {
            node = node.right;
        }
        
        return node.right;
    }
    
    /**
     * Returns the node lexicographically before the given node (or null if none).
     * 
     * This follows four simple branches:
     *  - If the uplink that returned us was a right uplink:
     *      - If predecessor's left is a valid uplink from predecessor, return it.
     *      - Else, follow the right path from the predecessor's left.
     *  - If the uplink that returned us was a left uplink:
     *      - Loop back through parents until we encounter a node where 
     *        node != node.parent.left.
     *          - If node.parent.left is uplink from node.parent:
     *              - If node.parent.left is not root, return it.
     *              - If it is root & root isEmpty, return null.
     *              - If it is root & root !isEmpty, return root.
     *          - If node.parent.left is not uplink from node.parent:
     *              - Follow right path for first right child from node.parent.left   
     * 
     * @param start
     */
    TrieEntry<K, V> previousEntry(TrieEntry<K, V> start) {
        if (start.predecessor == null) {
            throw new IllegalArgumentException("must have come from somewhere!");
        }
        
        if (start.predecessor.right == start) {
            if (isValidUplink(start.predecessor.left, start.predecessor)) {
                return start.predecessor.left;
            } else {
                return followRight(start.predecessor.left);
            }
        } else {
            TrieEntry<K, V> node = start.predecessor;
            while (node.parent != null && node == node.parent.left) {
                node = node.parent;
            }
            
            if (node.parent == null) { // can be null if we're looking up root.
                return null;
            }
            
            if (isValidUplink(node.parent.left, node.parent)) {
                if (node.parent.left == root) {
                    if (root.isEmpty()) {
                        return null;
                    } else {
                        return root;
                    }
                    
                } else {
                    return node.parent.left;
                }
            } else {
                return followRight(node.parent.left);
            }
        }
    }
    
    /**
     * Returns the entry lexicographically after the given entry.
     * If the given entry is null, returns the first node.
     * 
     * This will traverse only within the subtree.  If the given node
     * is not within the subtree, this will have undefined results.
     */
    TrieEntry<K, V> nextEntryInSubtree(TrieEntry<K, V> node, 
            TrieEntry<K, V> parentOfSubtree) {
        if (node == null) {
            return firstEntry();
        } else {
            return nextEntryImpl(node.predecessor, node, parentOfSubtree);
        }
    }
    
    /**
     * A range view of the {@link Trie}
     */
    private abstract class RangeMap extends AbstractMap<K, V> 
            implements SortedMap<K, V> {

        /**
         * The {@link #entrySet()} view
         */
        private transient volatile Set<Map.Entry<K, V>> entrySet;

        /**
         * Creates and returns an {@link #entrySet()} 
         * view of the {@link RangeMap}
         */
        protected abstract Set<Map.Entry<K, V>> createEntrySet();

        /**
         * Returns the FROM Key
         */
        protected abstract K getFromKey();
        
        /**
         * Whether or not the {@link #getFromKey()} is in the range
         */
        protected abstract boolean isFromInclusive();
        
        /**
         * Returns the TO Key
         */
        protected abstract K getToKey();
        
        /**
         * Whether or not the {@link #getToKey()} is in the range
         */
        protected abstract boolean isToInclusive();
        
        /**
         * {@inheritDoc}
         */
        public Comparator<? super K> comparator() {
            return PatriciaTrie.this.comparator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsKey(Object key) {
            if (!inRange(castKey(key))) {
                return false;
            }

            return PatriciaTrie.this.containsKey(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V remove(Object key) {
            if (!inRange(castKey(key))) {
                return null;
            }

            return PatriciaTrie.this.remove(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get(Object key) {
            if (!inRange(castKey(key))) {
                return null;
            }

            return PatriciaTrie.this.get(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V put(K key, V value) {
            if (!inRange(key)) {
                throw new IllegalArgumentException(
                        "Key is out of range: " + key);
            }

            return PatriciaTrie.this.put(key, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            if (entrySet == null) {
                entrySet = createEntrySet();
            }
            return entrySet;
        }

        /**
         * {@inheritDoc}
         */
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            if (!inRange2(fromKey)) {
                throw new IllegalArgumentException(
                        "FromKey is out of range: " + fromKey);
            }

            if (!inRange2(toKey)) {
                throw new IllegalArgumentException(
                        "ToKey is out of range: " + toKey);
            }

            return createRangeMap(fromKey, isFromInclusive(), 
                    toKey, isToInclusive());
        }

        /**
         * {@inheritDoc}
         */
        public SortedMap<K, V> headMap(K toKey) {
            if (!inRange2(toKey)) {
                throw new IllegalArgumentException(
                        "ToKey is out of range: " + toKey);
            }

            return createRangeMap(getFromKey(), isFromInclusive(), 
                    toKey, isToInclusive());
        }

        /**
         * {@inheritDoc}
         */
        public SortedMap<K, V> tailMap(K fromKey) {
            if (!inRange2(fromKey)) {
                throw new IllegalArgumentException(
                        "FromKey is out of range: " + fromKey);
            }

            return createRangeMap(fromKey, isFromInclusive(), 
                    getToKey(), isToInclusive());
        }

        /**
         * Returns true if the provided key is greater than TO and
         * less than FROM
         */
        protected boolean inRange(K key) {

            K fromKey = getFromKey();
            K toKey = getToKey();

            return (fromKey == null || inFromRange(key, false))
                    && (toKey == null || inToRange(key, false));
        }

        /**
         * This form allows the high endpoint (as well as all legit keys)
         */
        protected boolean inRange2(K key) {

            K fromKey = getFromKey();
            K toKey = getToKey();

            return (fromKey == null || inFromRange(key, false))
                    && (toKey == null || inToRange(key, true));
        }

        /**
         * Returns true if the provided key is in the FROM range 
         * of the {@link RangeMap}
         */
        protected boolean inFromRange(K key, boolean forceInclusive) {

            K fromKey = getFromKey();
            boolean fromInclusive = isFromInclusive();

            int ret = keyAnalyzer.compare(key, fromKey);
            if (fromInclusive || forceInclusive) {
                return ret >= 0;
            } else {
                return ret > 0;
            }
        }

        /**
         * Returns true if the provided key is in the TO range 
         * of the {@link RangeMap}
         */
        protected boolean inToRange(K key, boolean forceInclusive) {

            K toKey = getToKey();
            boolean toInclusive = isToInclusive();

            int ret = keyAnalyzer.compare(key, toKey);
            if (toInclusive || forceInclusive) {
                return ret <= 0;
            } else {
                return ret < 0;
            }
        }

        /**
         * Creates and returns a sub-range view of the current {@link RangeMap}
         */
        protected abstract SortedMap<K, V> createRangeMap(K fromKey,
                boolean fromInclusive, K toKey, boolean toInclusive);
    }
   
   /**
    * A {@link RangeMap} that deals with {@link Entry}s
    */
   private class RangeEntryMap extends RangeMap {
       
       /** 
        * The key to start from, null if the beginning. 
        */
       protected final K fromKey;
       
       /** 
        * The key to end at, null if till the end. 
        */
       protected final K toKey;
       
       /** 
        * Whether or not the 'from' is inclusive. 
        */
       protected final boolean fromInclusive;
       
       /** 
        * Whether or not the 'to' is inclusive. 
        */
       protected final boolean toInclusive;
       
       /**
        * Creates a {@link RangeEntryMap} with the fromKey included and
        * the toKey excluded from the range
        */
       protected RangeEntryMap(K fromKey, K toKey) {
           this(fromKey, true, toKey, false);
       }
       
       /**
        * Creates a {@link RangeEntryMap}
        */
       protected RangeEntryMap(K fromKey, boolean fromInclusive, 
               K toKey, boolean toInclusive) {
           
           if (fromKey == null && toKey == null) {
               throw new IllegalArgumentException("must have a from or to!");
           }
           
           if (fromKey != null && toKey != null 
                   && keyAnalyzer.compare(fromKey, toKey) > 0) {
               throw new IllegalArgumentException("fromKey > toKey");
           }
           
           this.fromKey = fromKey;
           this.fromInclusive = fromInclusive;
           this.toKey = toKey;
           this.toInclusive = toInclusive;
       }
       
       /**
        * {@inheritDoc}
        */
       public K firstKey() {
           Map.Entry<K,V> e = null;
           if (fromKey == null) {
               e = firstEntry();
           } else {
               if (fromInclusive) {
                   e = ceilingEntry(fromKey);
               } else {
                   e = higherEntry(fromKey);
               }
           }
           
           K first = e != null ? e.getKey() : null;
           if (e == null || toKey != null && !inToRange(first, false)) {
               throw new NoSuchElementException();
           }
           return first;
       }

       /**
        * {@inheritDoc}
        */
       public K lastKey() {
           Map.Entry<K,V> e;
           if (toKey == null) {
               e = lastEntry();
           } else {
               if (toInclusive) {
                   e = floorEntry(toKey);
               } else {
                   e = lowerEntry(toKey);
               }
           }
           
           K last = e != null ? e.getKey() : null;
           if (e == null || fromKey != null && !inFromRange(last, false)) {
               throw new NoSuchElementException();
           }
           return last;
       }
       
       /**
        * {@inheritDoc}
        */
       @Override
       protected Set<Entry<K, V>> createEntrySet() {
           return new RangeEntrySet(this);
       }
       
       /**
        * {@inheritDoc}
        */
       @Override
       public K getFromKey() {
           return fromKey;
       }

       /**
        * {@inheritDoc}
        */
       @Override
       public K getToKey() {
           return toKey;
       }

       /**
        * {@inheritDoc}
        */
       @Override
       public boolean isFromInclusive() {
           return fromInclusive;
       }

       /**
        * {@inheritDoc}
        */
       @Override
       public boolean isToInclusive() {
           return toInclusive;
       }

       /**
        * {@inheritDoc}
        */
       @Override
       protected SortedMap<K, V> createRangeMap(K fromKey, boolean fromInclusive,
               K toKey, boolean toInclusive) {
           return new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
       }
   }
   
    /**
     * A {@link Set} view of a {@link RangeMap}
     */
    private class RangeEntrySet extends AbstractSet<Map.Entry<K, V>> {

        private final RangeMap delegate;

        private transient int size = -1;

        private transient int expectedModCount;

        /**
         * Creates a {@link RangeEntrySet}
         */
        public RangeEntrySet(RangeMap delegate) {
            if (delegate == null) {
                throw new NullPointerException("delegate");
            }

            this.delegate = delegate;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            K fromKey = delegate.getFromKey();
            K toKey = delegate.getToKey();

            TrieEntry<K, V> first = null;
            if (fromKey == null) {
                first = firstEntry();
            } else {
                first = ceilingEntry(fromKey);
            }

            TrieEntry<K, V> last = null;
            if (toKey != null) {
                last = ceilingEntry(toKey);
            }

            return new EntryIterator(first, last);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            if (size == -1 || expectedModCount != PatriciaTrie.this.modCount) {
                size = 0;

                for (Iterator<?> it = iterator(); it.hasNext(); it.next()) {
                    ++size;
                }

                expectedModCount = PatriciaTrie.this.modCount;
            }
            return size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty() {
            return !iterator().hasNext();
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            if (!delegate.inRange(key)) {
                return false;
            }

            TrieEntry<K, V> node = getEntry(key);
            return node != null && compare(node.getValue(), entry.getValue());
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            if (!delegate.inRange(key)) {
                return false;
            }

            TrieEntry<K, V> node = getEntry(key);
            if (node != null && compare(node.getValue(), entry.getValue())) {
                removeEntry(node);
                return true;
            }
            return false;
        }
        
        /** 
         * An {@link Iterator} for {@link RangeEntrySet}s. 
         */
        private final class EntryIterator extends TrieIterator<Map.Entry<K,V>> {
            
            private final K excludedKey;

            /**
             * Creates a {@link EntryIterator}
             */
            private EntryIterator(
                    TrieEntry<K,V> first, 
                    TrieEntry<K,V> last) {
                super(first);
                
                this.excludedKey = (last != null ? last.getKey() : null);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return next != null && !compare(next.key, excludedKey);
            }

            /**
             * {@inheritDoc}
             */
            public Map.Entry<K,V> next() {
                if (next == null || compare(next.key, excludedKey)) {
                    throw new NoSuchElementException();
                }
                
                return nextEntry();
            }
        }
    }   
   
    /** 
     * A submap used for prefix views over the {@link Trie}. 
     */
    private class PrefixRangeMap extends RangeMap {
        
        private final K prefix;
        
        private final int offsetInBits;
        
        private final int lengthInBits;
        
        private K fromKey = null;
        
        private K toKey = null;
        
        private transient int expectedModCount = 0;
        
        private int size = -1;
        
        /**
         * Creates a {@link PrefixRangeMap}
         */
        private PrefixRangeMap(K prefix, int offsetInBits, int lengthInBits) {
            this.prefix = prefix;
            this.offsetInBits = offsetInBits;
            this.lengthInBits = lengthInBits;
        }
        
        /**
         * This method does two things. It determinates the FROM
         * and TO range of the {@link PrefixRangeMap} and the number
         * of elements in the range. This method must be called every 
         * time the {@link Trie} has changed.
         */
        private int fixup() {
            // The trie has changed since we last
            // found our toKey / fromKey
            if (size == - 1 || PatriciaTrie.this.modCount != expectedModCount) {
                Iterator<Map.Entry<K, V>> it = entrySet().iterator();
                size = 0;
                
                Map.Entry<K, V> entry = null;
                if (it.hasNext()) {
                    entry = it.next();
                    size = 1;
                }
                
                fromKey = entry == null ? null : entry.getKey();
                if (fromKey != null) {
                    TrieEntry<K, V> prior = previousEntry((TrieEntry<K, V>)entry);
                    fromKey = prior == null ? null : prior.getKey();
                }
                
                toKey = fromKey;
                
                while (it.hasNext()) {
                    ++size;
                    entry = it.next();
                }
                
                toKey = entry == null ? null : entry.getKey();
                
                if (toKey != null) {
                    entry = nextEntry((TrieEntry<K, V>)entry);
                    toKey = entry == null ? null : entry.getKey();
                }
                
                expectedModCount = PatriciaTrie.this.modCount;
            }
            
            return size;
        }
        
        /**
         * {@inheritDoc}
         */
        public K firstKey() {
            fixup();
            
            Map.Entry<K,V> e = null;
            if (fromKey == null) {
                e = firstEntry();
            } else {
                e = higherEntry(fromKey);
            }
            
            K first = e != null ? e.getKey() : null;
            if (e == null || !keyAnalyzer.isPrefix(prefix, 
                    offsetInBits, lengthInBits, first)) {
                throw new NoSuchElementException();
            }
            
            return first;
        }

        /**
         * {@inheritDoc}
         */
        public K lastKey() {
            fixup();
            
            Map.Entry<K,V> e = null;
            if (toKey == null) {
                e = lastEntry();
            } else {
                e = lowerEntry(toKey);
            }
            
            K last = e != null ? e.getKey() : null;
            if (e == null || !keyAnalyzer.isPrefix(prefix, 
                    offsetInBits, lengthInBits, last)) {
                throw new NoSuchElementException();
            }
            
            return last;
        }
        
        /**
         * Returns true if this {@link PrefixRangeMap}'s key is a prefix
         * of the provided key.
         */
        @Override
        protected boolean inRange(K key) {
            return keyAnalyzer.isPrefix(prefix, offsetInBits, lengthInBits, key);
        }

        /**
         * Same as {@link #inRange(Object)}
         */
        @Override
        protected boolean inRange2(K key) {
            return inRange(key);
        }
        
        /**
         * Returns true if the provided Key is in the FROM range
         * of the {@link PrefixRangeMap}
         */
        @Override
        protected boolean inFromRange(K key, boolean forceInclusive) {
            return keyAnalyzer.isPrefix(prefix, offsetInBits, lengthInBits, key);
        }
        
        /**
         * Returns true if the provided Key is in the TO range
         * of the {@link PrefixRangeMap}
         */
        @Override
        protected boolean inToRange(K key, boolean forceInclusive) {
            return keyAnalyzer.isPrefix(prefix, offsetInBits, lengthInBits, key);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return new PrefixRangeEntrySet(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public K getFromKey() {
            return fromKey;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public K getToKey() {
            return toKey;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFromInclusive() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isToInclusive() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected SortedMap<K, V> createRangeMap(
                K fromKey, boolean fromInclusive,
                K toKey, boolean toInclusive) {
            return new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
        }
    }
    
    /**
     * A prefix {@link RangeEntrySet} view of the {@link Trie}
     */
    private final class PrefixRangeEntrySet extends RangeEntrySet {
        
        private final PrefixRangeMap delegate;
        
        private TrieEntry<K, V> prefixStart;
        
        private int expectedModCount = 0;
        
        /**
         * Creates a {@link PrefixRangeEntrySet}
         */
        public PrefixRangeEntrySet(PrefixRangeMap delegate) {
            super(delegate);
            this.delegate = delegate;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return delegate.fixup();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<Map.Entry<K,V>> iterator() {
            if (PatriciaTrie.this.modCount != expectedModCount) {
                prefixStart = subtree(delegate.prefix, delegate.offsetInBits, delegate.lengthInBits);
                expectedModCount = PatriciaTrie.this.modCount;
            }
            
            if (prefixStart == null) {
                Set<Map.Entry<K,V>> empty = Collections.emptySet();
                return empty.iterator();
            } else if (delegate.lengthInBits >= prefixStart.bitIndex) {
                return new SingletonIterator(prefixStart);
            } else {
                return new EntryIterator(prefixStart, delegate.prefix, delegate.offsetInBits, delegate.lengthInBits);
            }
        }
        
        /** 
         * An {@link Iterator} that holds a single {@link TrieEntry}. 
         */
        private final class SingletonIterator implements Iterator<Map.Entry<K, V>> {
            
            private final TrieEntry<K, V> entry;
            
            private int hit = 0;
            
            public SingletonIterator(TrieEntry<K, V> entry) {
                this.entry = entry;
            }
            
            /**
             * {@inheritDoc}
             */
            public boolean hasNext() {
                return hit == 0;
            }

            /**
             * {@inheritDoc}
             */
            public Map.Entry<K, V> next() {
                if (hit != 0) {
                    throw new NoSuchElementException();
                }
                
                ++hit;
                return entry;
            }

            /**
             * {@inheritDoc}
             */
            public void remove() {
                if (hit != 1) {
                    throw new IllegalStateException();
                }
                
                ++hit;
                PatriciaTrie.this.removeEntry(entry);
            }
        }
        
        /** 
         * An {@link Iterator} for iterating over a prefix search. 
         */
        private final class EntryIterator extends TrieIterator<Map.Entry<K, V>> {
            
            // values to reset the subtree if we remove it.
            protected final K prefix; 
            protected final int offset;
            protected final int lengthInBits;
            protected boolean lastOne;
            
            protected TrieEntry<K, V> subtree; // the subtree to search within
            
            /**
             * Starts iteration at the given entry & search only 
             * within the given subtree.
             */
            EntryIterator(TrieEntry<K, V> startScan, K prefix, 
                    int offset, int lengthInBits) {
                subtree = startScan;
                next = PatriciaTrie.this.followLeft(startScan);
                this.prefix = prefix;
                this.offset = offset;
                this.lengthInBits = lengthInBits;
            }

            /**
             * {@inheritDoc}
             */
            public Map.Entry<K,V> next() {
                Map.Entry<K, V> entry = nextEntry();
                if (lastOne) {
                    next = null;
                }
                return entry;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            protected TrieEntry<K, V> findNext(TrieEntry<K, V> prior) {
                return PatriciaTrie.this.nextEntryInSubtree(prior, subtree);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public void remove() {
                // If the current entry we're removing is the subtree
                // then we need to find a new subtree parent.
                boolean needsFixing = false;
                int bitIdx = subtree.bitIndex;
                if (current == subtree) {
                    needsFixing = true;
                }
                
                super.remove();
                
                // If the subtree changed its bitIndex or we
                // removed the old subtree, get a new one.
                if (bitIdx != subtree.bitIndex || needsFixing) {
                    subtree = subtree(prefix, offset, lengthInBits);
                }
                
                // If the subtree's bitIndex is less than the
                // length of our prefix, it's the last item
                // in the prefix tree.
                if (lengthInBits >= subtree.bitIndex) {
                    lastOne = true;
                }
            }
        }
    }
}
