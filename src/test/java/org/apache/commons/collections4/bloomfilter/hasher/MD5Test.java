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

public class MD5Test {

    @Test
    public void test() throws Exception {
        MD5 md5 = new MD5();
        long l1 = 0x8b1a9953c4611296L;
        long l2 = 0xa827abf8c47804d7L;
        byte[] buffer = "Hello".getBytes();

        long l = md5.applyAsLong(buffer, 0);
        assertEquals(l1, l);
        l = md5.applyAsLong(buffer, 1);
        assertEquals(l1 + l2, l);
        l = md5.applyAsLong(buffer, 2);
        assertEquals(l1 + l2 + l2, l);
    }

}
