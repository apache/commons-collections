/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.trie.analyzer;

import org.apache.commons.collections4.trie.KeyAnalyzer;

/**
 * An {@link KeyAnalyzer} for {@link String}s.
 * <p>
 * This class is stateless.
 * </p>
 *
 * @since 4.0
 */
public class StringKeyAnalyzer extends KeyAnalyzer<String> {

    private static final long serialVersionUID = -7032449491269434877L;

    /** A singleton instance of {@link StringKeyAnalyzer}. */
    public static final StringKeyAnalyzer INSTANCE = new StringKeyAnalyzer();

    /** The number of bits per {@link Character} plus a presence bit. */
    public static final int LENGTH = Character.SIZE + 1;

    /** A bit mask where the first bit is 1 and the others are zero. */
    private static final int MSB = 0x8000;

    /** Returns a bit mask where the given bit is set. */
    private static int mask(final int bit) {
        return MSB >>> bit;
    }

    /**
     * Constructs a new instance.
     *
     * @deprecated Use {@link #INSTANCE}.
     */
    @Deprecated
    public StringKeyAnalyzer() {
        // empty
    }

    @Override
    public int bitIndex(final String key, final int offsetInBits, final int lengthInBits,
                        final String other, final int otherOffsetInBits, final int otherLengthInBits) {

        if (offsetInBits % LENGTH != 0 || otherOffsetInBits % LENGTH != 0
                || lengthInBits % LENGTH != 0 || otherLengthInBits % LENGTH != 0) {
            throw new IllegalArgumentException("The offsets and lengths must be at Character boundaries");
        }

        final int beginIndex1 = offsetInBits / LENGTH;
        final int beginIndex2 = otherOffsetInBits / LENGTH;

        final int endIndex1 = beginIndex1 + lengthInBits / LENGTH;
        final int endIndex2 = other == null ? beginIndex2 : beginIndex2 + otherLengthInBits / LENGTH;

        final int length = Math.max(endIndex1, endIndex2);

        for (int i = 0; i < length; i++) {
            final int index1 = beginIndex1 + i;
            final int index2 = beginIndex2 + i;

            if (index1 < endIndex1 && other != null && index2 < endIndex2) {
                final char k = key.charAt(index1);
                final char f = other.charAt(index2);

                if (k != f) {
                    final int x = k ^ f;
                    return i * LENGTH + 1 + Integer.numberOfLeadingZeros(x) - (LENGTH - 1);
                }
            } else {
                // One has ended, the other has not. They differ at the presence bit of this block.
                return i * LENGTH;
            }
        }

        if (lengthInBits == 0 && (other == null || otherLengthInBits == 0)) {
            return NULL_BIT_KEY;
        }

        // Both keys are equal
        return EQUAL_BIT_KEY;
    }

    @Override
    public int bitsPerElement() {
        return LENGTH;
    }

    @Override
    public boolean isBitSet(final String key, final int bitIndex, final int lengthInBits) {
        if (key == null || bitIndex >= lengthInBits) {
            return false;
        }

        final int index = bitIndex / LENGTH;
        final int bit = bitIndex % LENGTH;

        if (bit == 0) {
            return true;
        }
        return (key.charAt(index) & mask(bit - 1)) != 0;
    }

    @Override
    public boolean isPrefix(final String prefix, final int offsetInBits,
                            final int lengthInBits, final String key) {
        if (offsetInBits % LENGTH != 0 || lengthInBits % LENGTH != 0) {
            throw new IllegalArgumentException(
                    "Cannot determine prefix outside of Character boundaries");
        }

        final String s1 = prefix.substring(offsetInBits / LENGTH, lengthInBits / LENGTH);
        return key.startsWith(s1);
    }

    @Override
    public int lengthInBits(final String key) {
        return key != null ? key.length() * LENGTH : 0;
    }
}
