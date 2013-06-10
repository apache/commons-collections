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
package org.apache.commons.collections4.trie.analyzer;

import org.apache.commons.collections4.trie.KeyAnalyzer;

/**
 * A {@link KeyAnalyzer} for {@link Long}s.
 *
 * @since 4.0
 * @version $Id$
 */
public class LongKeyAnalyzer extends KeyAnalyzer<Long> {

    private static final long serialVersionUID = -4119639247588227409L;

    /** A singleton instance of {@link LongKeyAnalyzer}. */
    public static final LongKeyAnalyzer INSTANCE = new LongKeyAnalyzer();

    /** The length of an {@link Long} in bits. */
    public static final int LENGTH = Long.SIZE;

    /** A bit mask where the first bit is 1 and the others are zero. */
    private static final long MSB = 0x8000000000000000L;

    /** Returns a bit mask where the given bit is set. */
    private static long mask(final int bit) {
        return MSB >>> bit;
    }

    public int bitsPerElement() {
        return 1;
    }

    public int lengthInBits(final Long key) {
        return LENGTH;
    }

    public boolean isBitSet(final Long key, final int bitIndex, final int lengthInBits) {
        return (key.longValue() & mask(bitIndex)) != 0;
    }

    public int bitIndex(final Long key, final int offsetInBits, final int lengthInBits,
                        final Long other, final int otherOffsetInBits, final int otherLengthInBits) {

        if (offsetInBits != 0 || otherOffsetInBits != 0) {
            throw new IllegalArgumentException("offsetInBits=" + offsetInBits
                    + ", otherOffsetInBits=" + otherOffsetInBits);
        }

        final long keyValue = key.longValue();
        if (keyValue == 0L) {
            return NULL_BIT_KEY;
        }

        final long otherValue = other != null ? other.longValue() : 0L;

        if (keyValue != otherValue) {
            final long xorValue = keyValue ^ otherValue;
            for (int i = 0; i < LENGTH; i++) {
                if ((xorValue & mask(i)) != 0L) {
                    return i;
                }
            }
        }

        return KeyAnalyzer.EQUAL_BIT_KEY;
    }

    public boolean isPrefix(final Long prefix, final int offsetInBits,
                            final int lengthInBits, final Long key) {

        final long value1 = prefix.longValue() << offsetInBits;
        final long value2 = key.longValue();

        long mask = 0L;
        for (int i = 0; i < lengthInBits; i++) {
            mask |= 0x1L << i;
        }

        return (value1 & mask) == (value2 & mask);
    }
}
