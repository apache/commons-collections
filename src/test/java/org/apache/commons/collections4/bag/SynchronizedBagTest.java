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
package org.apache.commons.collections4.bag;

import junit.framework.Test;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.BulkTest;

/**
 * Extension of {@link AbstractBagTest} for exercising the {@link SynchronizedBag}
 * implementation.
 *
 * @since 4.0
 */
public class SynchronizedBagTest<T> extends AbstractBagTest<T> {

    public SynchronizedBagTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(SynchronizedBagTest.class);
    }


    @Override
    public Bag<T> makeObject() {
        return SynchronizedBag.synchronizedBag(new HashBag<T>());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        Bag<T> bag = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/SynchronizedBag.emptyCollection.version4.obj");
//        bag = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/SynchronizedBag.fullCollection.version4.obj");
//    }

}
