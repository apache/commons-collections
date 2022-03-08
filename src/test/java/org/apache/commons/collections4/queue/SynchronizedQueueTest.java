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
package org.apache.commons.collections4.queue;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.collections4.BulkTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Extension of {@link AbstractQueueTest} for exercising the {@link SynchronizedQueue} implementation.
 *
 * @since 4.2
 */
public class SynchronizedQueueTest<T> extends AbstractQueueTest<T> {

    public static junit.framework.Test suite() {
        return BulkTest.makeSuite(SynchronizedQueueTest.class);
    }

    public SynchronizedQueueTest(final String testName) {
        super(testName);
    }

    // -----------------------------------------------------------------------

    @Override
    public String getCompatibilityVersion() {
        return "4.2";
    }

    @Override
    public Queue<T> makeObject() {
        return SynchronizedQueue.synchronizedQueue(new LinkedList<T>());
    }

    @Test
    @Ignore("Run once")
    public void testCreate() throws Exception {
        Queue<T> queue = makeObject();
        writeExternalFormToDisk((java.io.Serializable) queue,
            BulkTest.TEST_DATA_PATH + "SynchronizedQueue.emptyCollection.version4.2.obj");
        queue = makeFullCollection();
        writeExternalFormToDisk((java.io.Serializable) queue,
            BulkTest.TEST_DATA_PATH + "SynchronizedQueue.fullCollection.version4.2.obj");
    }

}
