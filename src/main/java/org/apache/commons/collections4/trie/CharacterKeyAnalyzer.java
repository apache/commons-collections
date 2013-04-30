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
 * A {@link KeyAnalyzer} for {@link Character}s.
 * 
 * @since 4.0
 * @version $Id$
 */
public class CharacterKeyAnalyzer extends AbstractKeyAnalyzer<Character> {
    
    private static final long serialVersionUID = 3928565962744720753L;
    
    /**
     * A singleton instance of the {@link CharacterKeyAnalyzer}.
     */
    public static final CharacterKeyAnalyzer INSTANCE 
        = new CharacterKeyAnalyzer();
    
    /**
     * The length of a {@link Character} in bits
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
        return 1;
    }
    
    /**
     * {@inheritDoc}
     */
    public int lengthInBits(final Character key) {
        return LENGTH;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBitSet(final Character key, final int bitIndex, final int lengthInBits) {
        return (key & mask(bitIndex)) != 0;
    }

    /**
     * {@inheritDoc}
     */
    public int bitIndex(final Character key, final int offsetInBits, final int lengthInBits, 
            final Character other, final int otherOffsetInBits, final int otherLengthInBits) {
        
        if (offsetInBits != 0 || otherOffsetInBits != 0) {
            throw new IllegalArgumentException("offsetInBits=" + offsetInBits 
                    + ", otherOffsetInBits=" + otherOffsetInBits);
        }
        
        final char keyValue = key.charValue();
        if (keyValue == Character.MIN_VALUE) {
            return NULL_BIT_KEY;
        }

        final char otherValue = other != null ? other.charValue() : Character.MIN_VALUE;
        
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
    public boolean isPrefix(final Character prefix, final int offsetInBits, 
            final int lengthInBits, final Character key) {
        
        final int value1 = prefix.charValue() << offsetInBits;
        final int value2 = key.charValue();
        
        int mask = 0;
        for(int i = 0; i < lengthInBits; i++) {
            mask |= 0x1 << i;
        }
        
        return (value1 & mask) == (value2 & mask);
    }
}
