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
package org.apache.commons.collections4.bloomfilter.hasher;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher.Builder;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Assert;
import org.junit.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Tests the
 * {@link org.apache.commons.collections4.bloomfilter.hasher.Hasher.Builder Hasher.Builder}.
 */
public class HasherBuilderTest {

    /**
     * Simple class to collect byte[] items added to the builder.
     */
    private static class TestBuilder implements Hasher.Builder {
        ArrayList<byte[]> items = new ArrayList<>();

        @Override
        public Hasher build() {
            throw new NotImplementedException("Not required");
        }

        @Override
        public Builder with(byte[] item) {
            items.add(item);
            return this;
        }
    }

    /**
     * Tests that adding CharSequence items works correctly.
     */
    @Test
    public void withCharSequenceTest() {
        final String ascii = "plain";
        final String extended = getExtendedString();
        for (final String s : new String[] {ascii, extended}) {
            for (final Charset cs : new Charset[] {
                StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8, StandardCharsets.UTF_16
            }) {
                TestBuilder builder = new TestBuilder();
                builder.with(s, cs);
                Assert.assertArrayEquals(s.getBytes(cs), builder.items.get(0));
            }
        }
    }

    /**
     * Tests that adding unencoded CharSequence items works correctly.
     */
    @Test
    public void withUnencodedCharSequenceTest() {
        final String ascii = "plain";
        final String extended = getExtendedString();
        for (final String s : new String[] {ascii, extended}) {
            final TestBuilder builder = new TestBuilder();
            builder.withUnencoded(s);
            final byte[] encoded = builder.items.get(0);
            final char[] original = s.toCharArray();
            // Should be twice the length
            Assert.assertEquals(original.length * 2, encoded.length);
            // Should be little endian (lower bits first)
            final CharBuffer buffer = ByteBuffer.wrap(encoded)
                                                .order(ByteOrder.LITTLE_ENDIAN).asCharBuffer();
            for (int i = 0; i < original.length; i++) {
                Assert.assertEquals(original[i], buffer.get(i));
            }
        }
    }

    /**
     * Gets a string with non-standard characters.
     *
     * @return the extended string
     */
    static String getExtendedString() {
        final char[] data = {'e', 'x', 't', 'e', 'n', 'd', 'e', 'd', ' ',
            // Add some characters that are non standard
            // non-ascii
            0xCA98,
            // UTF-16 surrogate pair
            0xD803, 0xDE6D
            // Add other cases here ...
        };
        return String.valueOf(data);
    }
}
