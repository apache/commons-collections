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

import java.util.Map;

/** 
 * Defines a map that holds a collection of values against each key.
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
 * Collection coll = (Collection) mhm.get(key);</pre>
 * <p>
 * <code>coll</code> will be a list containing "A", "B", "C".
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.11 $ $Date: 2004/02/18 01:15:42 $
 * 
 * @author Christopher Berry
 * @author James Strachan
 * @author Stephen Colebourne
 */
public interface MultiMap extends Map {
    
    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * 
     * @param key  the key to remove from
     * @param item  the item to remove
     * @return the value removed (which was passed in)
     */
    public Object remove(Object key, Object item);
   
}
