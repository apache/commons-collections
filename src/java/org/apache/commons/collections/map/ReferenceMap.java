/*
 *  Copyright 2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultMapEntry;

/**
 * A <code>Map</code> implementation that allows mappings to be
 * removed by the garbage collector.
 * <p>
 * When you construct a <code>ReferenceMap</code>, you can specify what kind
 * of references are used to store the map's keys and values.
 * If non-hard references are used, then the garbage collector can remove
 * mappings if a key or value becomes unreachable, or if the JVM's memory is
 * running low. For information on how the different reference types behave,
 * see {@link Reference}.
 * <p>
 * Different types of references can be specified for keys and values.
 * The keys can be configured to be weak but the values hard,
 * in which case this class will behave like a
 * <a href="http://java.sun.com/j2se/1.4/docs/api/java/util/WeakHashMap.html">
 * <code>WeakHashMap</code></a>. However, you can also specify hard keys and
 * weak values, or any other combination. The default constructor uses
 * hard keys and soft values, providing a memory-sensitive cache.
 * <p>
 * This {@link Map} implementation does <i>not</i> allow null elements.
 * Attempting to add a null key or value to the map will raise a <code>NullPointerException</code>.
 * <p>
 * As usual, this implementation is not synchronized.
 * You can use {@link java.util.Collections#synchronizedMap} to 
 * provide synchronized access to a <code>ReferenceMap</code>.
 * <p>
 * NOTE: As from Commons Collections 3.1 this map extends <code>AbstractHashedMap</code>
 * (previously it extended AbstractMap). As a result, the implementation is now
 * extensible and provides a <code>MapIterator</code>.
 *
 * @see java.lang.ref.Reference
 * 
 * @since Commons Collections 3.0 (previously in main package v2.1)
 * @version $Revision: 1.11 $ $Date: 2004/04/01 00:07:48 $
 * 
 * @author Paul Jack
 * @author Stephen Colebourne
 */
public class ReferenceMap extends AbstractHashedMap {

    /**
     *  For serialization.
     */
    private static final long serialVersionUID = -3370601314380922368L;

    /**
     *  Constant indicating that hard references should be used.
     */
    public static final int HARD = 0;

    /**
     *  Constant indicating that soft references should be used.
     */
    public static final int SOFT = 1;

    /**
     *  Constant indicating that weak references should be used.
     */
    public static final int WEAK = 2;

    // --- serialized instance variables:

    /**
     *  The reference type for keys.  Must be HARD, SOFT, WEAK.
     *  Note: I originally marked this field as final, but then this class
     *   didn't compile under JDK1.2.2.
     *  @serial
     */
    private int keyType;

    /**
     *  The reference type for values.  Must be HARD, SOFT, WEAK.
     *  Note: I originally marked this field as final, but then this class
     *   didn't compile under JDK1.2.2.
     *  @serial
     */
    private int valueType;

    /**
     *  The threshold variable is calculated by multiplying
     *  table.length and loadFactor.  
     *  Note: I originally marked this field as final, but then this class
     *   didn't compile under JDK1.2.2.
     *  @serial
     */
    private float loadFactor;
    
    /**
     * Should the value be automatically purged when the associated key has been collected?
     */
    private boolean purgeValues = false;

    // -- Non-serialized instance variables

    /**
     *  ReferenceQueue used to eliminate stale mappings.
     *  See purge.
     */
    private transient ReferenceQueue queue = new ReferenceQueue();

    /**
     *  Constructs a new <code>ReferenceMap</code> that will
     *  use hard references to keys and soft references to values.
     */
    public ReferenceMap() {
        this(HARD, SOFT);
    }

    /**
     *  Constructs a new <code>ReferenceMap</code> that will
     *  use the specified types of references.
     *
     *  @param keyType  the type of reference to use for keys;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     *  @param valueType  the type of reference to use for values;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     *  @param purgeValues should the value be automatically purged when the 
     *   key is garbage collected 
     */
    public ReferenceMap(int keyType, int valueType, boolean purgeValues) {
        this(keyType, valueType);
        this.purgeValues = purgeValues;
    }

    /**
     *  Constructs a new <code>ReferenceMap</code> that will
     *  use the specified types of references.
     *
     *  @param keyType  the type of reference to use for keys;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     *  @param valueType  the type of reference to use for values;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     */
    public ReferenceMap(int keyType, int valueType) {
        this(keyType, valueType, 16, 0.75f);
    }

    /**
     *  Constructs a new <code>ReferenceMap</code> with the
     *  specified reference types, load factor and initial
     *  capacity.
     *
     *  @param keyType  the type of reference to use for keys;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     *  @param valueType  the type of reference to use for values;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     *  @param capacity  the initial capacity for the map
     *  @param loadFactor  the load factor for the map
     *  @param purgeValues should the value be automatically purged when the 
     *   key is garbage collected 
     */
    public ReferenceMap(int keyType, int valueType, int capacity, 
                        float loadFactor, boolean purgeValues) {
        this(keyType, valueType, capacity, loadFactor);
        this.purgeValues = purgeValues;
    }

    /**
     *  Constructs a new <code>ReferenceMap</code> with the
     *  specified reference types, load factor and initial
     *  capacity.
     *
     *  @param keyType  the type of reference to use for keys;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     *  @param valueType  the type of reference to use for values;
     *   must be {@link #HARD}, {@link #SOFT}, {@link #WEAK}
     *  @param capacity  the initial capacity for the map
     *  @param loadFactor  the load factor for the map
     */
    public ReferenceMap(int keyType, int valueType, int capacity, float loadFactor) {
        super(capacity, loadFactor);

        verify("keyType", keyType);
        verify("valueType", valueType);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    /**
     * Checks the type int is a valid value.
     * 
     * @param name  the name for error messages
     * @param type  the type value to check
     * @throws IllegalArgumentException if the value if invalid
     */
    private static void verify(String name, int type) {
        if ((type < HARD) || (type > WEAK)) {
            throw new IllegalArgumentException(name + " must be HARD, SOFT, WEAK.");
        }
    }

    /**
     *  Writes this object to the given output stream.
     *
     *  @param out  the output stream to write to
     *  @throws IOException  if the stream raises it
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(data.length);

        // Have to use null-terminated list because size might shrink
        // during iteration

        for (Iterator iter = entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeObject(null);
    }


    /**
     *  Reads the contents of this object from the given input stream.
     *
     *  @param in  the input stream to read from
     *  @throws IOException  if the stream raises it
     *  @throws ClassNotFoundException  if the stream raises it
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        data = new HashEntry[in.readInt()];
        threshold = calculateThreshold(data.length, loadFactor);
        queue = new ReferenceQueue();
        Object key = in.readObject();
        while (key != null) {
            Object value = in.readObject();
            put(key, value);
            key = in.readObject();
        }
    }

    /**
     * Gets the entry mapped to the key specified.
     * 
     * @param key  the key
     * @return the entry, null if no match
     * @since Commons Collections 3.1
     */
    protected HashEntry getEntry(Object key) {
        if (key == null) {
            return null;
        } else {
            return super.getEntry(key);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Purges stale mappings from this map before read operations.
     * <p>
     * This implementation calls {@link #purge()} to maintain a consistent state.
     * 
     * @since Commons Collections 3.1
     */
    protected void purgeBeforeRead() {
        purge();
    }

    /**
     * Purges stale mappings from this map before write operations.
     * <p>
     * This implementation calls {@link #purge()} to maintain a consistent state.
     * 
     * @since Commons Collections 3.1
     */
    protected void purgeBeforeWrite() {
        purge();
    }

    /**
     * Purges stale mappings from this map.
     * <p>
     * Note that this method is not synchronized!  Special
     * care must be taken if, for instance, you want stale
     * mappings to be removed on a periodic basis by some
     * background thread.
     * 
     * @since Commons Collections 3.1
     */
    protected void purge() {
        Reference ref = queue.poll();
        while (ref != null) {
            purge(ref);
            ref = queue.poll();
        }
    }

    private void purge(Reference ref) {
        // The hashCode of the reference is the hashCode of the
        // mapping key, even if the reference refers to the 
        // mapping value...
        int hash = ref.hashCode();
        int index = hashIndex(hash, data.length);
        HashEntry previous = null;
        HashEntry entry = data[index];
        while (entry != null) {
            if (((ReferenceEntry) entry).purge(ref)) {
                if (previous == null) {
                    data[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                this.size--;
                return;
            }
            previous = entry;
            entry = entry.next;
        }

    }

    //-----------------------------------------------------------------------
    /**
     * Gets the size of the map.
     * 
     * @return the size
     */
    public int size() {
        purgeBeforeRead();
        return super.size();
    }

    /**
     * Checks whether the map is currently empty.
     * 
     * @return true if the map is currently size zero
     */
    public boolean isEmpty() {
        purgeBeforeRead();
        return super.isEmpty();
    }

    /**
     * Checks whether the map contains the specified key.
     * 
     * @param key  the key to search for
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key) {
        purgeBeforeRead();
        Entry entry = getEntry(key);
        if (entry == null) {
            return false;
        }
        return (entry.getValue() != null);
    }

    /**
     * Checks whether the map contains the specified value.
     * 
     * @param value  the value to search for
     * @return true if the map contains the value
     */
    public boolean containsValue(Object value) {
        purgeBeforeRead();
        if (value == null) {
            return false;
        }
        return super.containsValue(value);
    }

    /**
     * Gets the value mapped to the key specified.
     * 
     * @param key  the key
     * @return the mapped value, null if no match
     */
    public Object get(Object key) {
        purgeBeforeRead();
        Entry entry = getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }


    /**
     * Puts a key-value mapping into this map.
     * Neither the key nor the value may be null.
     * 
     * @param key  the key to add, must not be null
     * @param value  the value to add, must not be null
     * @return the value previously mapped to this key, null if none
     * @throws NullPointerException if either the key or value is null
     */
    public Object put(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("null keys not allowed");
        }
        if (value == null) {
            throw new NullPointerException("null values not allowed");
        }

        purgeBeforeWrite();
        return super.put(key, value);
    }
    
    /**
     * Removes the specified mapping from this map.
     * 
     * @param key  the mapping to remove
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(Object key) {
        if (key == null) {
            return null;
        }
        purgeBeforeWrite();
        return super.remove(key);
    }

    /**
     * Clears this map.
     */
    public void clear() {
        super.clear();
        while (queue.poll() != null) {} // drain the queue
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two keys, in internal converted form, to see if they are equal.
     * <p>
     * This implementation converts the key from the entry to a real reference
     * before comparison.
     * 
     * @param key1  the first key to compare passed in from outside
     * @param key2  the second key extracted from the entry via <code>entry.key</code>
     * @return true if equal
     * @since Commons Collections 3.1
     */
    protected boolean isEqualKey(Object key1, Object key2) {
        key2 = (keyType > HARD ? ((Reference) key2).get() : key2);
        return (key1 == key2 || key1.equals(key2));
    }
    
    //-----------------------------------------------------------------------
    /**
     * Creates a ReferenceEntry instead of a HashEntry.
     * 
     * @param next  the next entry in sequence
     * @param hashCode  the hash code to use
     * @param key  the key to store
     * @param value  the value to store
     * @return the newly created entry
     * @since Commons Collections 3.1
     */
    protected HashEntry createEntry(HashEntry next, int hashCode, Object key, Object value) {
        return new ReferenceEntry(this, next, hashCode, key, value);
    }

    /**
     * Creates an entry set iterator.
     * 
     * @return the entrySet iterator
     * @since Commons Collections 3.1
     */
    protected Iterator createEntrySetIterator() {
        return new ReferenceEntrySetIterator(this);
    }

    /**
     * Creates an key set iterator.
     * 
     * @return the keySet iterator
     * @since Commons Collections 3.1
     */
    protected Iterator createKeySetIterator() {
        return new ReferenceKeySetIterator(this);
    }

    /**
     * Creates an values iterator.
     * 
     * @return the values iterator
     * @since Commons Collections 3.1
     */
    protected Iterator createValuesIterator() {
        return new ReferenceValuesIterator(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a set view of this map's entries.
     * The <code>setValue()</code> method on the entries has no effect.
     *
     * @return a set view of this map's entries
     */
    public Set entrySet() {
        if (entrySet == null) {
            entrySet = new ReferenceEntrySet(this);
        }
        return entrySet;
    }
    
    /**
     * EntrySet implementation.
     */
    static class ReferenceEntrySet extends EntrySet {
        
        protected ReferenceEntrySet(AbstractHashedMap parent) {
            super(parent);
        }

        public Object[] toArray() {
            return toArray(new Object[0]);
        }

        public Object[] toArray(Object[] arr) {
            // special implementation to handle disappearing entries
            ArrayList list = new ArrayList();
            Iterator iterator = iterator();
            while (iterator.hasNext()) {
                Entry e = (Entry) iterator.next();
                list.add(new DefaultMapEntry(e.getKey(), e.getValue()));
            }
            return list.toArray(arr);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a set view of this map's keys.
     *
     * @return a set view of this map's keys
     */
    public Set keySet() {
        if (keySet == null) {
            keySet = new ReferenceKeySet(this);
        }
        return keySet;
    }
    
    /**
     * KeySet implementation.
     */
    static class ReferenceKeySet extends KeySet {
        
        protected ReferenceKeySet(AbstractHashedMap parent) {
            super(parent);
        }

        public Object[] toArray() {
            return toArray(new Object[0]);
        }

        public Object[] toArray(Object[] arr) {
            // special implementation to handle disappearing keys
            List list = new ArrayList(parent.size());
            for (Iterator it = iterator(); it.hasNext(); ) {
                list.add(it.next());
            }
            return list.toArray(arr);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a collection view of this map's values.
     *
     * @return a set view of this map's values
     */
    public Collection values() {
        if (values == null) {
            values = new ReferenceValues(this);
        }
        return values;
    }
    
    /**
     * Values implementation.
     */
    static class ReferenceValues extends Values {
        
        protected ReferenceValues(AbstractHashedMap parent) {
            super(parent);
        }

        public Object[] toArray() {
            return toArray(new Object[0]);
        }

        public Object[] toArray(Object[] arr) {
            // special implementation to handle disappearing values
            List list = new ArrayList(parent.size());
            for (Iterator it = iterator(); it.hasNext(); ) {
                list.add(it.next());
            }
            return list.toArray(arr);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A MapEntry implementation for the map.
     * <p>
     * If getKey() or getValue() returns null, it means
     * the mapping is stale and should be removed.
     */
    protected static class ReferenceEntry extends HashEntry {
        /** The parent map */
        protected final ReferenceMap parent;

        /**
         * Creates a new entry object for the ReferenceMap.
         * 
         * @param parent  the parent map
         * @param next  the next entry in the hash bucket
         * @param hashCode  the hash code of the key
         * @param key  the key
         * @param value  the value
         */
        public ReferenceEntry(ReferenceMap parent, HashEntry next, int hashCode, Object key, Object value) {
            super(next, hashCode, null, null);
            this.parent = parent;
            this.key = toReference(parent.keyType, key, hashCode);
            this.value = toReference(parent.valueType, value, hashCode);
        }

        public Object getKey() {
            return (parent.keyType > HARD) ? ((Reference) key).get() : key;
        }

        public Object getValue() {
            return (parent.valueType > HARD) ? ((Reference) value).get() : value;
        }

        public Object setValue(Object obj) {
            Object old = getValue();
            if (parent.valueType > HARD) {
                ((Reference)value).clear();
            }
            value = toReference(parent.valueType, obj, hashCode);
            return old;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            
            Map.Entry entry = (Map.Entry)obj;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if ((key == null) || (value == null)) {
                return false;
            }
            return key.equals(getKey()) && value.equals(getValue());
        }

        /**
         * Constructs a reference of the given type to the given referent.
         * The reference is registered with the queue for later purging.
         *
         * @param type  HARD, SOFT or WEAK
         * @param referent  the object to refer to
         * @param hash  the hash code of the <i>key</i> of the mapping;
         *    this number might be different from referent.hashCode() if
         *    the referent represents a value and not a key
         * @since Commons Collections 3.1
         */
        protected Object toReference(int type, Object referent, int hash) {
            switch (type) {
                case HARD: return referent;
                case SOFT: return new SoftRef(hash, referent, parent.queue);
                case WEAK: return new WeakRef(hash, referent, parent.queue);
                default: throw new Error();
            }
        }

        boolean purge(Reference ref) {
            boolean r = (parent.keyType > HARD) && (key == ref);
            r = r || ((parent.valueType > HARD) && (value == ref));
            if (r) {
                if (parent.keyType > HARD) {
                    ((Reference)key).clear();
                }
                if (parent.valueType > HARD) {
                    ((Reference)value).clear();
                } else if (parent.purgeValues) {
                    value = null;
                }
            }
            return r;
        }
        
        ReferenceEntry next() {
            return (ReferenceEntry) next;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * The EntrySet iterator.
     */
    static class ReferenceEntrySetIterator implements Iterator {
        /** The parent map */
        final ReferenceMap parent;
        
        // These fields keep track of where we are in the table.
        int index;
        ReferenceEntry entry;
        ReferenceEntry previous;

        // These Object fields provide hard references to the
        // current and next entry; this assures that if hasNext()
        // returns true, next() will actually return a valid element.
        Object nextKey, nextValue;
        Object currentKey, currentValue;

        int expectedModCount;

        public ReferenceEntrySetIterator(ReferenceMap parent) {
            super();
            this.parent = parent;
            index = (parent.size() != 0 ? parent.data.length : 0);
            // have to do this here!  size() invocation above
            // may have altered the modCount.
            expectedModCount = parent.modCount;
        }

        public boolean hasNext() {
            checkMod();
            while (nextNull()) {
                ReferenceEntry e = entry;
                int i = index;
                while ((e == null) && (i > 0)) {
                    i--;
                    e = (ReferenceEntry) parent.data[i];
                }
                entry = e;
                index = i;
                if (e == null) {
                    currentKey = null;
                    currentValue = null;
                    return false;
                }
                nextKey = e.getKey();
                nextValue = e.getValue();
                if (nextNull()) {
                    entry = entry.next();
                }
            }
            return true;
        }

        private void checkMod() {
            if (parent.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        private boolean nextNull() {
            return (nextKey == null) || (nextValue == null);
        }

        protected Entry nextEntry() {    
            checkMod();
            if (nextNull() && !hasNext()) {
                throw new NoSuchElementException();
            }
            previous = entry;
            entry = entry.next();
            currentKey = nextKey;
            currentValue = nextValue;
            nextKey = null;
            nextValue = null;
            return previous;
        }

        public Object next() {
            return nextEntry();
        }

        public void remove() {
            checkMod();
            if (previous == null) {
                throw new IllegalStateException();
            }
            parent.remove(currentKey);
            previous = null;
            currentKey = null;
            currentValue = null;
            expectedModCount = parent.modCount;
        }
    }

    /**
     * The keySet iterator.
     */
    static class ReferenceKeySetIterator extends ReferenceEntrySetIterator {
        
        ReferenceKeySetIterator(ReferenceMap parent) {
            super(parent);
        }
        
        public Object next() {
            return nextEntry().getKey();
        }
    }

    /**
     * The values iterator.
     */
    static class ReferenceValuesIterator extends ReferenceEntrySetIterator {
        
        ReferenceValuesIterator(ReferenceMap parent) {
            super(parent);
        }
        
        public Object next() {
            return nextEntry().getValue();
        }
    }

    //-----------------------------------------------------------------------
    // These two classes store the hashCode of the key of
    // of the mapping, so that after they're dequeued a quick
    // lookup of the bucket in the table can occur.

    /**
     * A soft reference holder.
     */
    static class SoftRef extends SoftReference {
        private int hash;

        public SoftRef(int hash, Object r, ReferenceQueue q) {
            super(r, q);
            this.hash = hash;
        }

        public int hashCode() {
            return hash;
        }
    }

    /**
     * A weak reference holder.
     */
    static class WeakRef extends WeakReference {
        private int hash;

        public WeakRef(int hash, Object r, ReferenceQueue q) {
            super(r, q);
            this.hash = hash;
        }

        public int hashCode() {
            return hash;
        }
    }


}
