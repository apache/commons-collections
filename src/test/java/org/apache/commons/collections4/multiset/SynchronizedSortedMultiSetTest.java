/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.multiset;

import org.apache.commons.collections4.SortedMultiSet;

/**
 * Extension of {@link AbstractSortedMultiSetTest} for exercising the
 * {@link SynchronizedSortedMultiSet} implementation.
 */
public class SynchronizedSortedMultiSetTest<T> extends AbstractSortedMultiSetTest<T> {

    @Override
    public String getCompatibilityVersion() {
        return "4.6";
    }

    @Override
    public SortedMultiSet<T> makeObject() {
        return SynchronizedSortedMultiSet.synchronizedSortedMultiSet(new TreeMultiSet<>());
    }

//    void testCreate() throws Exception {
//        MultiSet<T> multiset = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/SynchronizedSortedMultiSet.emptyCollection.version4.6.obj");
//        multiset = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) multiset, "src/test/resources/org/apache/commons/collections4/data/test/SynchronizedSortedMultiSet.fullCollection.version4.6.obj");
//    }

}
