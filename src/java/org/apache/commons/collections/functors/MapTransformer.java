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
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation that returns the value held in a specified map
 * using the input parameter as a key.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/02/18 00:59:20 $
 *
 * @author Stephen Colebourne
 */
public final class MapTransformer implements Transformer, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = 862391807045468939L;
    
    /** The map of data to lookup in */
    private final Map iMap;

    /**
     * Factory to create the transformer.
     * <p>
     * If the map is null, a transformer that always returns null is returned.
     * 
     * @param map the map, not cloned
     * @return the transformer
     */
    public static Transformer getInstance(Map map) {
        if (map == null) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        return new MapTransformer(map);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param map  the map to use for lookup, not cloned
     */
    private MapTransformer(Map map) {
        super();
        iMap = map;
    }

    /**
     * Returns the result by looking up in the map.
     */
    public Object transform(Object input) {
        return iMap.get(input);
    }
    
}
