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

import java.nio.ByteBuffer;
import java.util.Objects;

import org.junit.Test;

public class ObjectsHashTest {

    @Test
    public void test() throws Exception {
        ObjectsHash obj = new ObjectsHash();

        byte[] buffer = "Now is the time for all good men to come to the aid of their country".getBytes("UTF-8");


        long l = obj.applyAsLong(buffer, 0);
        long prev = 0;
        assertEquals(Objects.hash(prev, buffer), l);
        prev += l;
        l = obj.applyAsLong(buffer, 1);
        assertEquals(Objects.hash(prev, buffer), l);
        prev += l;
        l = obj.applyAsLong(buffer, 2);
        assertEquals(Objects.hash(prev, buffer), l);
    }

}
