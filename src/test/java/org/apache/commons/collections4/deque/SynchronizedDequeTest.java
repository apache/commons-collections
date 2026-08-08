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
package org.apache.commons.collections4.deque;

import junit.framework.Test;
import org.apache.commons.collections4.BulkTest;
import org.junit.Ignore;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Extension of {@link AbstractDequeTest} for exercising the
 * {@link SynchronizedDeque} implementation.
 *
 * @since 4.5
 */
public class SynchronizedDequeTest<E> extends AbstractDequeTest<E> {
    /**
     * JUnit constructor.
     *
     * @param testName the test class name
     */
    public SynchronizedDequeTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(SynchronizedDequeTest.class);
    }

    @Override
    public Deque<E> makeObject() {
        return SynchronizedDeque.synchronizedDeque(new LinkedList<E>());
    }

    @Override
    public String getCompatibilityVersion() {
        return "4.5";
    }

    @Ignore("Run once")
    public void testCreate() throws Exception {
        Deque<E> deque = makeObject();
        writeExternalFormToDisk((java.io.Serializable) deque, "src/test/resources/data/test/SynchronizedDeque.emptyCollection.version4.5.obj");
        deque = makeFullCollection();
        writeExternalFormToDisk((java.io.Serializable) deque, "src/test/resources/data/test/SynchronizedDeque.fullCollection.version4.5.obj");
    }
}
