/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.apache.commons.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/** 
 * <code>MultiHashMap</code> is the default implementation of the 
 * {@link org.apache.commons.collections.MultiMap MultiMap} interface.
 * <p>
 * A <code>MultiMap</code> is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that
 * key. Getting a value will always return a Collection, holding all the
 * values put to that key. This implementation uses an ArrayList as the 
 * collection.
 * <p>
 * For example:
 * <pre>
 * MultiMap mhm = new MultiHashMap();
 * mhm.put(key, "A");
 * mhm.put(key, "B");
 * mhm.put(key, "C");
 * Collection coll = mhm.get(key);</pre>
 * <p>
 * <code>coll</code> will be a list containing "A", "B", "C".
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.15 $ $Date: 2004/02/18 01:15:42 $
 * 
 * @author Christopher Berry
 * @author James Strachan
 * @author Steve Downey
 * @author Stephen Colebourne
 * @author Julien Buret
 * @author Serhiy Yevtushenko
 */
public class MultiHashMap extends HashMap implements MultiMap {
    
    //backed values collection
    private transient Collection values = null;
    
    // compatibility with commons-collection releases 2.0/2.1
    private static final long serialVersionUID = 1943563828307035349L;

    /**
     * Constructor.
     */
    public MultiHashMap() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param initialCapacity  the initial map capacity
     */
    public MultiHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructor.
     * 
     * @param initialCapacity  the initial map capacity
     * @param loadFactor  the amount 0.0-1.0 at which to resize the map
     */
    public MultiHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor.
     * 
     * @param mapToCopy  a Map to copy
     */
    public MultiHashMap(Map mapToCopy) {
        super(mapToCopy);
    }

    /**
     * Read the object during deserialization.
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        // This method is needed because the 1.2/1.3 Java deserialisation called
        // put and thus messed up that method
        
        // default read object
        s.defaultReadObject();

        // problem only with jvm <1.4
        String version = "1.2";
        try {
            version = System.getProperty("java.version");
        } catch (SecurityException ex) {
            // ignore and treat as 1.2/1.3
        }

        if (version.startsWith("1.2") || version.startsWith("1.3")) {
            for (Iterator iterator = entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                // put has created a extra collection level, remove it
                super.put(entry.getKey(), ((Collection) entry.getValue()).iterator().next());
            }
        }
    }
    
    /**
     * Put a key and value into the map.
     * <p>
     * The value is added to a collection mapped to the key instead of 
     * replacing the previous value.
     * 
     * @param key  the key to set
     * @param value  the value to set the key to
     * @return the value added if the add is successful, <code>null</code> otherwise
     */    
    public Object put(Object key, Object value) {
        // NOTE:: put is called during deserialization in JDK < 1.4 !!!!!!
        //        so we must have a readObject()
        Collection coll = (Collection) super.get(key);
        if (coll == null) {
            coll = createCollection(null);
            super.put(key, coll);
        }
        boolean results = coll.add(value);

        return (results ? value : null);
    }
    
    /**
     * Does the map contain a specific value.
     * <p>
     * This searches the collection mapped to each key, and thus could be slow.
     * 
     * @param value  the value to search for
     * @return true if the list contains the value
     */
    public boolean containsValue(Object value) {
        Set pairs = super.entrySet();

        if (pairs == null) {
            return false;
        }
        Iterator pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
            Collection coll = (Collection) keyValuePair.getValue();
            if (coll.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * 
     * @param key  the key to remove from
     * @param item  the value to remove
     * @return the value removed (which was passed in)
     */
    public Object remove(Object key, Object item) {
        Collection valuesForKey = (Collection) super.get(key);
        if (valuesForKey == null) {
            return null;
        }
        valuesForKey.remove(item);

        // remove the list if it is now empty
        // (saves space, and allows equals to work)
        if (valuesForKey.isEmpty()){
            remove(key);
        }
        return item;
    }

    /**
     * Clear the map.
     * <p>
     * This clears each collection in the map, and so may be slow.
     */
    public void clear() {
        // For gc, clear each list in the map
        Set pairs = super.entrySet();
        Iterator pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
            Collection coll = (Collection) keyValuePair.getValue();
            coll.clear();
        }
        super.clear();
    }

    /** 
     * Gets a view over all the values in the map.
     * <p>
     * The values view includes all the entries in the collections at each map key.
     * 
     * @return the collection view of all the values in the map
     */
    public Collection values() {
        Collection vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    /**
     * Inner class to view the elements.
     */
    private class Values extends AbstractCollection {

        public Iterator iterator() {
            return new ValueIterator();
        }

        public int size() {
            int compt = 0;
            Iterator it = iterator();
            while (it.hasNext()) {
                it.next();
                compt++;
            }
            return compt;
        }

        public void clear() {
            MultiHashMap.this.clear();
        }

    }

    /**
     * Inner iterator to view the elements.
     */
    private class ValueIterator implements Iterator {
        private Iterator backedIterator;
        private Iterator tempIterator;

        private ValueIterator() {
            backedIterator = MultiHashMap.super.values().iterator();
        }

        private boolean searchNextIterator() {
            while (tempIterator == null || tempIterator.hasNext() == false) {
                if (backedIterator.hasNext() == false) {
                    return false;
                }
                tempIterator = ((Collection) backedIterator.next()).iterator();
            }
            return true;
        }

        public boolean hasNext() {
            return searchNextIterator();
        }

        public Object next() {
            if (searchNextIterator() == false) {
                throw new NoSuchElementException();
            }
            return tempIterator.next();
        }

        public void remove() {
            if (tempIterator == null) {
                throw new IllegalStateException();
            }
            tempIterator.remove();
        }

    }

    /**
     * Clone the map.
     * <p>
     * The clone will shallow clone the collections as well as the map.
     * 
     * @return the cloned map
     */
    public Object clone() {
        MultiHashMap obj = (MultiHashMap) super.clone();

        // clone each Collection container
        for (Iterator it = entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Collection coll = (Collection) entry.getValue();
            Collection newColl = createCollection(coll);
            entry.setValue(newColl);
        }
        return obj;
    }
    
    /** 
     * Creates a new instance of the map value Collection container.
     * <p>
     * This method can be overridden to use your own collection type.
     *
     * @param coll  the collection to copy, may be null
     * @return the new collection
     */
    protected Collection createCollection(Collection coll) {
        if (coll == null) {
            return new ArrayList();
        } else {
            return new ArrayList(coll);
        }
    }

}
