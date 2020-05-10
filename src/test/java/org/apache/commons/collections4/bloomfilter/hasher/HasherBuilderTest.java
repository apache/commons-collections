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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Tests the
 * {@link org.apache.commons.collections4.bloomfilter.hasher.Hasher.Builder
 * Hasher.Builder}.
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
        for (final String s : new String[] { ascii, extended }) {
            for (final Charset cs : new Charset[] { StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8,
                StandardCharsets.UTF_16 }) {
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
    public void withUnecodedCharSequenceTest() {
        final String ascii = "plain";
        final String extended = getExtendedString();
        for (final String s : new String[] { ascii, extended }) {
            final TestBuilder builder = new TestBuilder();
            builder.withUnencoded(s);
            final byte[] encoded = builder.items.get(0);
            final char[] original = s.toCharArray();
            // Should be twice the length
            Assert.assertEquals(original.length * 2, encoded.length);
            // Should be little endian (lower bits first)
            final CharBuffer buffer = ByteBuffer.wrap(encoded).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer();
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
        final char[] data = { 'e', 'x', 't', 'e', 'n', 'd', 'e', 'd', ' ',
            // Add some characters that are non standard
            // non-ascii
            0xCA98,
            // UTF-16 surrogate pair
            0xD803, 0xDE6D
            // Add other cases here ...
        };
        return String.valueOf(data);
    }

    /**
     * Test that adding an integer into the hasher works correctly
     */
    @Test
    public void withIntTest() {
        TestBuilder builder = new TestBuilder();
        Integer[] values = { Integer.valueOf(0), Integer.MAX_VALUE, Integer.MIN_VALUE };
        for (int i : values) {
            builder.with(i);
        }

        for (int i = 0; i < values.length; i++) {
            Assert.assertArrayEquals(ByteBuffer.allocate(Integer.BYTES).putInt(values[i]).array(),
                    builder.items.get(i));
        }
    }

    /**
     * Test that adding a long into the hasher works correctly
     */
    @Test
    public void withLongTest() {
        TestBuilder builder = new TestBuilder();
        Long[] values = { Long.valueOf(0), Long.MAX_VALUE, Long.MIN_VALUE };
        for (long l : values) {
            builder.with(l);
        }

        for (int i = 0; i < values.length; i++) {
            Assert.assertArrayEquals(ByteBuffer.allocate(Long.BYTES).putLong(values[i]).array(), builder.items.get(i));
        }
    }

    /**
     * Test that adding a double into the hasher works correctly
     */
    @Test
    public void withDoubleTest() {
        TestBuilder builder = new TestBuilder();
        Double[] values = { Double.valueOf(0.0), Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_NORMAL,
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN };
        for (double d : values) {
            builder.with(d);
        }

        for (int i = 0; i < values.length; i++) {
            Assert.assertArrayEquals(ByteBuffer.allocate(Double.BYTES).putDouble(values[i]).array(),
                    builder.items.get(i));
        }
    }

    /**
     * Test that adding a float into the hasher works correctly
     */
    @Test
    public void withFloatTest() {
        TestBuilder builder = new TestBuilder();
        Float[] values = { Float.valueOf(0.0f), Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_NORMAL,
            Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN };
        for (float f : values) {
            builder.with(f);
        }

        for (int i = 0; i < values.length; i++) {
            Assert.assertArrayEquals(ByteBuffer.allocate(Float.BYTES).putFloat(values[i]).array(),
                    builder.items.get(i));
        }
    }

    /**
     * Test that adding a character into the hasher works correctly
     */
    @Test
    public void withCharTest() {
        TestBuilder builder = new TestBuilder();
        Character[] values = { Character.valueOf((char) 0), Character.MAX_HIGH_SURROGATE, Character.MAX_LOW_SURROGATE,
            Character.MAX_SURROGATE, Character.MAX_VALUE, Character.MIN_HIGH_SURROGATE, Character.MIN_LOW_SURROGATE,
            Character.MIN_SURROGATE, Character.MIN_VALUE };
        for (char c : values) {
            builder.with(c);
        }

        for (int i = 0; i < values.length; i++) {
            Assert.assertArrayEquals(ByteBuffer.allocate(Character.BYTES).putChar(values[i]).array(),
                    builder.items.get(i));
        }
    }

    /**
     * Test that adding a short into the hasher works correctly
     */
    @Test
    public void withShortTest() {
        TestBuilder builder = new TestBuilder();
        Short[] values = { Short.valueOf((short) 0), Short.MAX_VALUE, Short.MIN_VALUE };
        for (short s : values) {
            builder.with(s);
        }

        for (int i = 0; i < values.length; i++) {
            Assert.assertArrayEquals(ByteBuffer.allocate(Short.BYTES).putShort(values[i]).array(),
                    builder.items.get(i));
        }
    }

    /**
     * Test that adding a BigInteger into the hasher works correctly
     */
    @Test
    public void withBigIngeterTest() {
        TestBuilder builder = new TestBuilder();
        BigInteger[] values = { BigInteger.ZERO, BigInteger.ONE, BigInteger.TWO, BigInteger.TEN,
                BigInteger.valueOf(Long.MAX_VALUE), BigInteger.valueOf(Long.MIN_VALUE),
                BigInteger.valueOf(Long.MAX_VALUE).pow(3) };
        for (BigInteger bi : values) {
            builder.with(bi);
        }

        for (int i = 0; i < values.length; i++) {
            Assert.assertArrayEquals(values[i].toByteArray(), builder.items.get(i));
        }
    }

    /**
     * Test that adding a BigDecimal into the hasher works correctly
     */
    @Test
    public void withBigDecimalTest() {
        TestBuilder builder = new TestBuilder();
        // can not test Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, and
        // Double.NaN
        BigDecimal[] values = { BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.valueOf(Double.MAX_VALUE),
                BigDecimal.valueOf(Double.MIN_VALUE), BigDecimal.valueOf(Double.MIN_NORMAL),
                BigDecimal.valueOf(Long.MIN_VALUE, Integer.MIN_VALUE),
                BigDecimal.valueOf(Long.MIN_VALUE, Integer.MAX_VALUE),
                BigDecimal.valueOf(Long.MAX_VALUE, Integer.MIN_VALUE),
                BigDecimal.valueOf(Long.MAX_VALUE, Integer.MAX_VALUE), };

        for (BigDecimal bd : values) {
            builder.with(bd);
        }

        for (int i = 0; i < values.length; i++) {
            ByteBuffer buff = ByteBuffer.wrap(builder.items.get(i));
            Assert.assertEquals("Error in value " + i, values[i].scale(), buff.getInt());
            byte[] part = new byte[builder.items.get(i).length - Integer.BYTES];
            buff.get(part);
            Assert.assertArrayEquals("Error in value " + i, values[i].unscaledValue().toByteArray(), part);
        }
    }

}
