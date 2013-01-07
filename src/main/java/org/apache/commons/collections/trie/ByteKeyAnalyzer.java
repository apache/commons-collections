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
 * A {@link KeyAnalyzer} for {@link Byte}s.
 * 
 * @since 4.0
 * @version $Id$
 */
public class ByteKeyAnalyzer extends AbstractKeyAnalyzer<Byte> {
    
    private static final long serialVersionUID = 3395803342983289829L;

    /**
     * A singleton instance of {@link ByteKeyAnalyzer}
     */
    public static final ByteKeyAnalyzer INSTANCE = new ByteKeyAnalyzer();
    
    /**
     * The length of an {@link Byte} in bits
     */
    public static final int LENGTH = Byte.SIZE;
    
    /**
     * A bit mask where the first bit is 1 and the others are zero
     */
    private static final int MSB = 0x80;
    
    /**
     * Returns a bit mask where the given bit is set
     */
    private static int mask(int bit) {
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
    public int lengthInBits(Byte key) {
        return LENGTH;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBitSet(Byte key, int bitIndex, int lengthInBits) {
        return (key & mask(bitIndex)) != 0;
    }

    /**
     * {@inheritDoc}
     */
    public int bitIndex(Byte key, int offsetInBits, int lengthInBits, 
            Byte other, int otherOffsetInBits, int otherLengthInBits) {
        
        if (offsetInBits != 0 || otherOffsetInBits != 0) {
            throw new IllegalArgumentException("offsetInBits=" + offsetInBits 
                    + ", otherOffsetInBits=" + otherOffsetInBits);
        }
        
        byte keyValue = key.byteValue();
        if (keyValue == 0) {
            return NULL_BIT_KEY;
        }

        byte otherValue = other != null ? other.byteValue() : 0;
        
        if (keyValue != otherValue) {
            int xorValue = keyValue ^ otherValue;
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
    public boolean isPrefix(Byte prefix, int offsetInBits, 
            int lengthInBits, Byte key) {
        
        int value1 = prefix.byteValue() << offsetInBits;
        int value2 = key.byteValue();
        
        int mask = 0;
        for (int i = 0; i < lengthInBits; i++) {
            mask |= 0x1 << i;
        }
        
        return (value1 & mask) == (value2 & mask);
    }
}
