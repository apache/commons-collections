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
package org.apache.commons.collections4.trie;

/**
 * A {@link KeyAnalyzer} for {@link Integer}s.
 * 
 * @since 4.0
 * @version $Id$
 */
public class IntegerKeyAnalyzer extends AbstractKeyAnalyzer<Integer> {
    
    private static final long serialVersionUID = 4928508653722068982L;
    
    /**
     * A singleton instance of {@link IntegerKeyAnalyzer}
     */
    public static final IntegerKeyAnalyzer INSTANCE = new IntegerKeyAnalyzer();
    
    /**
     * The length of an {@link Integer} in bits
     */
    public static final int LENGTH = Integer.SIZE;
    
    /**
     * A bit mask where the first bit is 1 and the others are zero
     */
    private static final int MSB = 0x80000000;
    
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
        return 1;
    }
    
    /**
     * {@inheritDoc}
     */
    public int lengthInBits(final Integer key) {
        return LENGTH;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBitSet(final Integer key, final int bitIndex, final int lengthInBits) {
        return (key.intValue() & mask(bitIndex)) != 0;
    }

    /**
     * {@inheritDoc}
     */
    public int bitIndex(final Integer key, final int offsetInBits, final int lengthInBits, 
            final Integer other, final int otherOffsetInBits, final int otherLengthInBits) {
        
        if (offsetInBits != 0 || otherOffsetInBits != 0) {
            throw new IllegalArgumentException("offsetInBits=" + offsetInBits 
                    + ", otherOffsetInBits=" + otherOffsetInBits);
        }
        
        final int keyValue = key.intValue();
        if (keyValue == 0) {
            return NULL_BIT_KEY;
        }

        final int otherValue = other != null ? other.intValue() : 0;
        
        if (keyValue != otherValue) {
            final int xorValue = keyValue ^ otherValue;
            for (int i = 0; i < LENGTH; i++) {
                if ((xorValue & mask(i)) != 0) {
                    return i;
                }
            }
        }
        
        return KeyAnalyzer.EQUAL_BIT_KEY;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isPrefix(final Integer prefix, final int offsetInBits, 
            final int lengthInBits, final Integer key) {
        
        final int value1 = prefix.intValue() << offsetInBits;
        final int value2 = key.intValue();
        
        int mask = 0;
        for (int i = 0; i < lengthInBits; i++) {
            mask |= 0x1 << i;
        }
        
        return (value1 & mask) == (value2 & mask);
    }
}
