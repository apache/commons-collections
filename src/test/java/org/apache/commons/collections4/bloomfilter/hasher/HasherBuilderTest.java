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

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests the
 * {@link org.apache.commons.collections4.bloomfilter.hasher.Hasher.Builder Hasher.Builder}.
 */
@ExtendWith(MockitoExtension.class)
public class HasherBuilderTest {

    private static final String ASCII_TEXT = "plain";

    @Mock
    private Hasher.Builder builder;

    @Captor
    private ArgumentCaptor<byte[]> byteCaptor;

    /**
     * Tests that adding CharSequence items works correctly.
     */
    @ParameterizedTest
    @MethodSource("provideParamsForWithCharSequenceTest")
    public void withCharSequenceTest(String s, Charset cs) {
        when(builder.with(any())).thenReturn(builder);
        when(builder.with(s, cs)).thenCallRealMethod();

        builder.with(s, cs);

        verify(builder, times(1)).with(any());
        verify(builder).with(byteCaptor.capture());
        assertArrayEquals(s.getBytes(cs), byteCaptor.getValue());
    }

    /**
     * Tests that adding unencoded CharSequence items works correctly.
     */
    @ParameterizedTest
    @MethodSource("provideParamsForWithUnencodedCharSequenceTest")
    public void withUnencodedCharSequenceTest(String s) {
        when(builder.with(any())).thenReturn(builder);
        when(builder.withUnencoded(s)).thenCallRealMethod();

        builder.withUnencoded(s);

        verify(builder, times(1)).with(any());
        verify(builder).with(byteCaptor.capture());

        // Should be twice the length
        final char[] original = s.toCharArray();
        assertEquals(original.length * 2, byteCaptor.getValue().length);

        // Should be little endian (lower bits first)
        final CharBuffer buffer = ByteBuffer.wrap(byteCaptor.getValue())
                .order(ByteOrder.LITTLE_ENDIAN).asCharBuffer();
        for (int i = 0; i < original.length; i++) {
            assertEquals(original[i], buffer.get(i));
        }
    }

    /**
     * Gets a stream of arguments for test method: withCharSequenceTest.
     *
     * @return the stream of arguments
     */
    private static Stream<Arguments> provideParamsForWithCharSequenceTest() {
        return Stream.of(
            Arguments.of(ASCII_TEXT, StandardCharsets.ISO_8859_1),
            Arguments.of(ASCII_TEXT, StandardCharsets.UTF_8),
            Arguments.of(ASCII_TEXT, StandardCharsets.UTF_16),
            Arguments.of(getExtendedString(), StandardCharsets.ISO_8859_1),
            Arguments.of(getExtendedString(), StandardCharsets.UTF_8),
            Arguments.of(getExtendedString(), StandardCharsets.UTF_16)
        );
    }

    /**
     * Gets a stream of arguments for test method: withUnencodedCharSequenceTest.
     *
     * @return the stream of arguments
     */
    private static Stream<Arguments> provideParamsForWithUnencodedCharSequenceTest() {
        return Stream.of(
            Arguments.of(ASCII_TEXT),
            Arguments.of(getExtendedString())
        );
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
