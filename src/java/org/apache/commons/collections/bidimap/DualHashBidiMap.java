/*
 *  Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.collections.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.BidiMap;

/**
 * Implementation of <code>BidiMap</code> that uses two <code>HashMap</code> instances.
 * 
 * @since Commons Collections 3.0
 * @version $Id: DualHashBidiMap.java,v 1.6 2004/02/18 00:57:39 scolebourne Exp $
 * 
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public class DualHashBidiMap
        extends AbstractDualBidiMap implements Serializable {

    /** Ensure serialization compatibility */
    private static final long serialVersionUID = 721969328361808L;

    /**
     * Creates an empty <code>HashBidiMap</code>
     */
    public DualHashBidiMap() {
        super();
    }

    /** 
     * Constructs a <code>HashBidiMap</code> and copies the mappings from
     * specified <code>Map</code>.  
     *
     * @param map  the map whose mappings are to be placed in this map
     */
    public DualHashBidiMap(Map map) {
        super();
        putAll(map);
    }
    
    /** 
     * Constructs a <code>HashBidiMap</code> that decorates the specified maps.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseBidiMap  the inverse BidiMap
     */
    protected DualHashBidiMap(Map normalMap, Map reverseMap, BidiMap inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
    }

    /**
     * Creates a new instance of the map used by the subclass to store data.
     * 
     * @return the map to be used for internal storage
     */
    protected Map createMap() {
        return new HashMap();
    }

    /**
     * Creates a new instance of this object.
     * 
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseBidiMap  the inverse BidiMap
     * @return new bidi map
     */
    protected BidiMap createBidiMap(Map normalMap, Map reverseMap, BidiMap inverseBidiMap) {
        return new DualHashBidiMap(normalMap, reverseMap, inverseBidiMap);
    }

    // Serialization
    //-----------------------------------------------------------------------
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(maps[0]);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Map map = (Map) in.readObject();
        putAll(map);
    }

}
