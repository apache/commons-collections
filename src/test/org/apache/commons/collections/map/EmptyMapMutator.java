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
package org.apache.commons.collections.map;

import java.util.Map;
import java.util.Collection;

/**
 * This class is used in TestCompositeMap. When testing serialization, 
 * the class has to be separate of TestCompositeMap, else the test 
 * class also has to be serialized. 
 */
class EmptyMapMutator implements CompositeMap.MapMutator {
    public void resolveCollision(CompositeMap composite,
    Map existing,
    Map added,
    Collection intersect) {
        // Do nothing
    }
    
    public Object put(CompositeMap map, Map[] composited, Object key, Object value) {
        return composited[0].put(key, value);
    }
    
    public void putAll(CompositeMap map, Map[] composited, Map t) {
        composited[0].putAll(t);
    }
    
}
