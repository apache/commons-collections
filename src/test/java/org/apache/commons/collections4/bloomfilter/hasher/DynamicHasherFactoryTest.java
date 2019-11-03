/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.function.ToLongBiFunction;
import java.util.Set;

import org.apache.commons.collections4.bloomfilter.Hasher;
import org.junit.After;
import org.junit.Test;

public class DynamicHasherFactoryTest {

    DynamicHasher.Factory factory = new DynamicHasher.Factory();

    @Test
    public void testListFuncs() {
        Set<String> names = factory.listFunctionNames();
        assertEquals(4, names.size());
        assertTrue(names.contains(MD5.NAME));
        assertTrue(names.contains(Murmur32.NAME));
        assertTrue(names.contains(Murmur128.NAME));
        assertTrue(names.contains(ObjectsHash.NAME));
    }

    @Test
    public void testUseFunction() {
        Hasher h = factory.useFunction(MD5.NAME).build();
        assertEquals(MD5.NAME, h.getName());
    }

    @Test
    public void testUseFunction_InvalidName() {
        try {
            factory.useFunction(MD5.NAME+"X").build();
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }
}
