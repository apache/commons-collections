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
package org.apache.commons.collections.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link SynchronizedCollection} implementation.
 *
 * @since Commons Collections 3.1
 * @version $Revision$
 *
 * @author Phil Steitz
 * @author Stephen Colebourne
 */
public class SynchronizedCollectionTest<E> extends AbstractCollectionTest<E> {

    public SynchronizedCollectionTest(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public Collection<E> makeObject() {
        return SynchronizedCollection.synchronizedCollection(new ArrayList<E>());
    }

    @Override
    public Collection<E> makeConfirmedCollection() {
        return new ArrayList<E>();
    }

    @Override
    public Collection<E> makeConfirmedFullCollection() {
        ArrayList<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/SynchronizedCollection.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/SynchronizedCollection.fullCollection.version3.1.obj");
//    }

}
