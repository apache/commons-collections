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
import java.util.List;

/**
 * Extension of {@link AbstractTestCollection} for exercising the
 * {@link UnmodifiableCollection} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Phil Steitz
 * @author Stephen Colebourne
 */
public class TestUnmodifiableCollection<E> extends AbstractTestCollection<E> {

    public TestUnmodifiableCollection(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public Collection<E> makeObject() {
        return UnmodifiableCollection.unmodifiableCollection(new ArrayList<E>());
    }

    @Override
    public Collection<E> makeFullCollection() {
        List<E> list = new ArrayList<E>();
        list.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableCollection.unmodifiableCollection(list);
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
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnmodifiableCollection.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/UnmodifiableCollection.fullCollection.version3.1.obj");
//    }

}
