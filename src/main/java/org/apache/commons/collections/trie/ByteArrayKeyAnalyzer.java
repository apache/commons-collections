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
 * A {@link KeyAnalyzer} for byte[]s.
 * 
 * @since 4.0
 * @version $Id$
 */
public class ByteArrayKeyAnalyzer extends AbstractKeyAnalyzer<byte[]> {
    
    private static final long serialVersionUID = 7382825097492285877L;

    /**
     * A singleton instance of {@link ByteArrayKeyAnalyzer}
     */
    public static final ByteArrayKeyAnalyzer INSTANCE 
        = new ByteArrayKeyAnalyzer(Integer.MAX_VALUE);
    
    /**
     * The length of an {@link Byte} in bits
     */
    public static final int LENGTH = Byte.SIZE;
    
    /**
     * A bit mask where the first bit is 1 and the others are zero
     */
    private static final int MSB = 0x80;
    
    /**
     * A place holder for null
     */
    private static final byte[] NULL = new byte[0];
    
    /**
     * The maximum length of a key in bits
     */
    private final int maxLengthInBits;
    
    public ByteArrayKeyAnalyzer(int maxLengthInBits) {
        if (maxLengthInBits < 0) {
            throw new IllegalArgumentException(
                    "maxLengthInBits=" + maxLengthInBits);
        }
        
        this.maxLengthInBits = maxLengthInBits;
    }
    
    /**
     * Returns a bit mask where the given bit is set
     */
    private static int mask(int bit) {
        return MSB >>> bit;
    }

    /**
     * Returns the maximum length of a key in bits
     * @return the maximum key length in bits
     */
    public int getMaxLengthInBits() {
        return maxLengthInBits;
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
    public int lengthInBits(byte[] key) {
        return key != null ? key.length * bitsPerElement() : 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isBitSet(byte[] key, int bitIndex, int lengthInBits) {
        if (key == null) {     
            return false;
        }
        
        int prefix = maxLengthInBits - lengthInBits;
        int keyBitIndex = bitIndex - prefix;
        
        if (keyBitIndex >= lengthInBits || keyBitIndex < 0) {
            return false;
        }
        
        int index = keyBitIndex / LENGTH;
        int bit = keyBitIndex % LENGTH;
        return (key[index] & mask(bit)) != 0;
    }

    /**
     * {@inheritDoc}
     */
    public int bitIndex(byte[] key, int offsetInBits, int lengthInBits, 
            byte[] other, int otherOffsetInBits, int otherLengthInBits) {
        
        if (other == null) {
            other = NULL;
        }
        
        boolean allNull = true;
        int length = Math.max(lengthInBits, otherLengthInBits);
        int prefix = maxLengthInBits - length;
        
        if (prefix < 0) {
            return KeyAnalyzer.OUT_OF_BOUNDS_BIT_KEY;
        }
        
        for (int i = 0; i < length; i++) {
            int index = prefix + offsetInBits + i;
            boolean value = isBitSet(key, index, lengthInBits);
                
            if (value) {
                allNull = false;
            }
            
            int otherIndex = prefix + otherOffsetInBits + i;
            boolean otherValue = isBitSet(other, otherIndex, otherLengthInBits);
            
            if (value != otherValue) {
                return index;
            }
        }
        
        if (allNull) {
            return KeyAnalyzer.NULL_BIT_KEY;
        }
        
        return KeyAnalyzer.EQUAL_BIT_KEY;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isPrefix(byte[] prefix, int offsetInBits, 
            int lengthInBits, byte[] key) {
        
        int keyLength = lengthInBits(key);
        if (lengthInBits > keyLength) {
            return false;
        }
        
        int elements = lengthInBits - offsetInBits;
        for (int i = 0; i < elements; i++) {
            if (isBitSet(prefix, i+offsetInBits, lengthInBits) 
                    != isBitSet(key, i, keyLength)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(byte[] o1, byte[] o2) {
        if (o1 == null) {
            return o2 == null ? 0 : -1;
        } else if (o2 == null) {
            return o1 == null ? 0 : 1;
        }
        
        if (o1.length != o2.length) {
            return o1.length - o2.length;
        }
        
        for (int i = 0; i < o1.length; i++) {
            int diff = (o1[i] & 0xFF) - (o2[i] & 0xFF);
            if (diff != 0) {
                return diff;
            }
        }

        return 0;
    }
}
