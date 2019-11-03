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
import org.junit.Test;

public class Murmur128Test {

    @Test
    public void test() throws Exception {
        Murmur128 murmur = new Murmur128();

        long l1 = 0xe7eb60dabb386407L;
        long l2 = 0xc3ca49f691f73056L;
        byte[] buffer ="Now is the time for all good men to come to the aid of their country".getBytes("UTF-8");

        long l = murmur.applyAsLong(buffer, 0);
        assertEquals(l1, l);
        l = murmur.applyAsLong(buffer, 1);
        assertEquals(l1 + l2, l);
        l = murmur.applyAsLong(buffer, 2);
        assertEquals(l1 + l2 + l2, l);
    }

}
