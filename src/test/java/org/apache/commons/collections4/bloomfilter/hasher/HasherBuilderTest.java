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
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.ArgumentMatchers.any;

/**
 * Tests the
 * {@link org.apache.commons.collections4.bloomfilter.hasher.Hasher.Builder Hasher.Builder}.
 */
public class HasherBuilderTest {

    /**
     * Tests that adding CharSequence items works correctly.
     */
    @Test
    public void withCharSequenceTest() {
        final String ascii = "plain";
        final String extended = getExtendedString();
        for (final String s : new String[] { ascii, extended }) {
            for (final Charset cs : new Charset[] { StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8,
                StandardCharsets.UTF_16 }) {
                // Create variables for tracking behaviors of mock object
                ArrayList<byte[]> builderItems = new ArrayList<>();
                // Construct mock object
                final Builder builder = spy(Hasher.Builder.class);
                // Method Stubs
                doThrow(new NotImplementedException("Not required")).when(builder).build();
                doAnswer((stubInvo) -> {
                    byte[] item = stubInvo.getArgument(0);
                    builderItems.add(item);
                    return builder;
                }).when(builder).with(any(byte[].class));
                builder.with(s, cs);
                assertArrayEquals(s.getBytes(cs), builderItems.get(0));
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
        for (final String s : new String[] { ascii, extended }) {
            // Create variables for tracking behaviors of mock object
            ArrayList<byte[]> builderItems = new ArrayList<>();
            // Construct mock object
            final Builder builder = spy(Hasher.Builder.class);
            // Method Stubs
            doThrow(new NotImplementedException("Not required")).when(builder).build();
            doAnswer((stubInvo) -> {
                byte[] item = stubInvo.getArgument(0);
                builderItems.add(item);
                return builder;
            }).when(builder).with(any(byte[].class));
            builder.withUnencoded(s);
            final byte[] encoded = builderItems.get(0);
            final char[] original = s.toCharArray();
            // Should be twice the length
            assertEquals(original.length * 2, encoded.length);
            // Should be little endian (lower bits first)
            final CharBuffer buffer = ByteBuffer.wrap(encoded).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer();
            for (int i = 0; i < original.length; i++) {
                assertEquals(original[i], buffer.get(i));
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
