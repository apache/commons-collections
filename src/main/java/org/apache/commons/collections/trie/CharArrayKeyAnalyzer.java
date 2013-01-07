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
package org.apache.commons.collections.trie;

/**
 * An {@link KeyAnalyzer} for {@code char[]}s.
 * 
 * @since 4.0
 * @version $Id$
 */
public class CharArrayKeyAnalyzer extends AbstractKeyAnalyzer<char[]> {
    
    private static final long serialVersionUID = -8167897361549463457L;

    /**
     * A singleton instance of {@link CharArrayKeyAnalyzer}
     */
    public static final CharArrayKeyAnalyzer INSTANCE = new CharArrayKeyAnalyzer();

    /**
     * The number of bits per {@link Character}
     */
    public static final int LENGTH = Character.SIZE;

    /**
     * A bit mask where the first bit is 1 and the others are zero
     */
    private static final int MSB = 0x8000;

    /**
     * Returns a bit mask where the given bit is set
     */
    private static int mask(final int bit) {
        return MSB >>> bit;
    }

    /**
     * {@inheritDoc}
     */
    public int bitsPerElement() {
        return LENGTH;
    }

    /**
     * {@inheritDoc}
     */
    public int lengthInBits(final char[] key) {
        return key != null ? key.length * LENGTH : 0;
    }

    /**
     * {@inheritDoc}
     */
    public int bitIndex(final char[] key, final int offsetInBits, final int lengthInBits,
            final char[] other, final int otherOffsetInBits, final int otherLengthInBits) {
        boolean allNull = true;

        if (offsetInBits % LENGTH != 0 || otherOffsetInBits % LENGTH != 0
                || lengthInBits % LENGTH != 0 || otherLengthInBits % LENGTH != 0) {
            throw new IllegalArgumentException(
                    "The offsets and lengths must be at Character boundaries");
        }


        final int beginIndex1 = offsetInBits / LENGTH;
        final int beginIndex2 = otherOffsetInBits / LENGTH;

        final int endIndex1 = beginIndex1 + lengthInBits / LENGTH;
        final int endIndex2 = beginIndex2 + otherLengthInBits / LENGTH;

        final int length = Math.max(endIndex1, endIndex2);

        // Look at each character, and if they're different
        // then figure out which bit makes the difference
        // and return it.
        char k = 0, f = 0;
        for(int i = 0; i < length; i++) {
            final int index1 = beginIndex1 + i;
            final int index2 = beginIndex2 + i;

            if (index1 >= endIndex1) {
                k = 0;
            } else {
                k = key[index1];
            }

            if (other == null || index2 >= endIndex2) {
                f = 0;
            } else {
                f = other[index2];
            }

            if (k != f) {
               final int x = k ^ f;
               return i * LENGTH + Integer.numberOfLeadingZeros(x) - LENGTH;
            }

            if (k != 0) {
                allNull = false;
            }
        }

        // All bits are 0
        if (allNull) {
            return KeyAnalyzer.NULL_BIT_KEY;
        }

        // Both keys are equal
        return KeyAnalyzer.EQUAL_BIT_KEY;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBitSet(final char[] key, final int bitIndex, final int lengthInBits) {
        if (key == null || bitIndex >= lengthInBits) {
            return false;
        }

        final int index = bitIndex / LENGTH;
        final int bit = bitIndex % LENGTH;

        return (key[index] & mask(bit)) != 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPrefix(final char[] prefix, final int offsetInBits,
            final int lengthInBits, final char[] key) {
        if (offsetInBits % LENGTH != 0 || lengthInBits % LENGTH != 0) {
            throw new IllegalArgumentException(
                    "Cannot determine prefix outside of Character boundaries");
        }

        final int off = offsetInBits / LENGTH;
        final int len = lengthInBits / LENGTH;
        for (int i = 0; i < len; i ++) {
            if (prefix[i + off] != key[i]) {
                return false;
            }
        }
        return true;
    }
}
