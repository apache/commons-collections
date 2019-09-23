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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertEquals;
import java.nio.ByteBuffer;

import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter;
import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter.Builder;
import org.apache.commons.collections4.bloomfilter.ProtoBloomFilter.Hash;
import org.junit.Test;

public class BuilderTest {

    private static final Hash HELLO_HASH = new Hash(3871253994707141660L, -6917270852172884668L);

    @Test
    public void updateTest_byte() {
        Builder builder = ProtoBloomFilter.builder().with((byte) 0x1);
        assertEquals(1, builder.getHashes().size());
        assertEquals(new Hash(8849112093580131862L, 8613248517421295493L), builder.getHashes().iterator().next());
    }

    @Test
    public void updateTest_byteArray() {
        Builder builder = ProtoBloomFilter.builder().with("Hello".getBytes());
        assertEquals(1, builder.getHashes().size());
        assertEquals(HELLO_HASH, builder.getHashes().iterator().next());
    }

    @Test
    public void updateTest_ByteBuffer() {
        Builder builder = ProtoBloomFilter.builder().with(ByteBuffer.wrap("Hello".getBytes()));
        assertEquals(1, builder.getHashes().size());
        assertEquals(HELLO_HASH, builder.getHashes().iterator().next());
    }

    @Test
    public void updateTest_ProtoBloomFilter() {
        ProtoBloomFilter proto = ProtoBloomFilter.builder().with("Hello").build();
        Builder builder = ProtoBloomFilter.builder().with(proto);
        assertEquals(1, builder.getHashes().size());
        assertEquals(HELLO_HASH, builder.getHashes().iterator().next());
    }

    @Test
    public void updateTest_String() {
        Builder builder = ProtoBloomFilter.builder().with("Hello");
        assertEquals(1, builder.getHashes().size());
        assertEquals(HELLO_HASH, builder.getHashes().iterator().next());
    }

    @Test
    public void buildTest_byte() {
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build((byte) 0x1);
        assertEquals(1, proto.getItemCount());
        assertEquals(new Hash(8849112093580131862L, 8613248517421295493L), proto.getHashes().iterator().next());
    }

    @Test
    public void buildTest_byteArray() {
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build("Hello".getBytes());
        assertEquals(1, proto.getItemCount());
        assertEquals(HELLO_HASH, proto.getHashes().iterator().next());
    }

    @Test
    public void buildTest_ByteBuffer() {
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build(ByteBuffer.wrap("Hello".getBytes()));
        assertEquals(1, proto.getItemCount());
        assertEquals(HELLO_HASH, proto.getHashes().iterator().next());
    }

    @Test
    public void buildTest_ProtoBloomFilter() {
        ProtoBloomFilter proto1 = ProtoBloomFilter.builder().with("Hello").build();
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build(proto1);
        assertEquals(1, proto.getItemCount());
        assertEquals(HELLO_HASH, proto.getHashes().iterator().next());
    }

    @Test
    public void buildTest_String() {
        ProtoBloomFilter proto = ProtoBloomFilter.builder().build("Hello");
        assertEquals(1, proto.getItemCount());
        assertEquals(HELLO_HASH, proto.getHashes().iterator().next());
    }

    @Test
    public void updateTest_LongString() {
        Builder builder = ProtoBloomFilter.builder().with("Now is the time for all good men to come to the aid of their country");
        assertEquals(1, builder.getHashes().size());
        assertEquals(new Hash(-1735186738861022201L, -4338573967658373034L), builder.getHashes().iterator().next());
    }
}
