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
package org.apache.commons.collections.set;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class is used in CompositeSetTest. When testing serialization, 
 * the class has to be separate of CompositeSetTest, else the test 
 * class also has to be serialized. 
 */
class EmptySetMutator<E> implements CompositeSet.SetMutator<E> {

    /** Serialization version */
    private static final long serialVersionUID = 5321193666420238910L;

    private final Set<E> contained;

    public EmptySetMutator(final Set<E> set) {
        this.contained = set;
    }

    public void resolveCollision(final CompositeSet<E> comp, final Set<E> existing, final Set<E> added, final Collection<E> intersects) {
        throw new IllegalArgumentException();
    }
    
    public boolean add(final CompositeSet<E> composite, final List<Set<E>> collections, final E obj) {
        return contained.add(obj);
    }
    
    public boolean addAll(final CompositeSet<E> composite, final List<Set<E>> collections, final Collection<? extends E> coll) {
        return contained.addAll(coll);
    }    
}
