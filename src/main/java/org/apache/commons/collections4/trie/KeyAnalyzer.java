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

import java.io.Serializable;
import java.util.Comparator;

/** 
 * Defines the interface to analyze {@link org.apache.commons.collections4.Trie Trie} keys on a bit level. 
 * {@link KeyAnalyzer}'s methods return the length of the key in bits, 
 * whether or not a bit is set, and bits per element in the key. 
 * <p>
 * Additionally, a method determines if a key is a prefix of another 
 * key and returns the bit index where one key is different from another 
 * key (if the key and found key are equal than the return value is 
 * {@link #EQUAL_BIT_KEY}).
 * 
 * @since 4.0
 * @version $Id$
 */
public interface KeyAnalyzer<K> extends Comparator<K>, Serializable {
    
    /** 
     * Returned by {@link #bitIndex(Object, int, int, Object, int, int)} 
     * if key's bits are all 0 
     */
    public static final int NULL_BIT_KEY = -1;
    
    /** 
     * Returned by {@link #bitIndex(Object, int, int, Object, int, int)} 
     * if key and found key are equal. This is a very very specific case 
     * and shouldn't happen on a regular basis
     */
    public static final int EQUAL_BIT_KEY = -2;
    
    public static final int OUT_OF_BOUNDS_BIT_KEY = -3;
    
    /**
     * Returns the number of bits per element in the key.
     * This is only useful for variable-length keys, such as Strings.
     * 
     * @return the number of bits per element
     */
    public int bitsPerElement();
    
    /** 
     * Returns the length of the Key in bits.
     * 
     * @param key  the key
     * @return the bit length of the key
     */
    public int lengthInBits(K key);
    
    /** 
     * Returns whether or not a bit is set.
     */
    public boolean isBitSet(K key, int bitIndex, int lengthInBits);
    
    /**
     * Returns the n-th different bit between key and found. This starts the comparison in
     * key at 'keyStart' and goes for 'keyLength' bits, and compares to the found key starting
     * at 'foundStart' and going for 'foundLength' bits.
     */
    public int bitIndex(K key, int offsetInBits, int lengthInBits,
                        K other, int otherOffsetInBits, int otherLengthInBits);
    
    /**
     * Determines whether or not the given prefix (from offset to length) is a prefix of the given key.
     */
    public boolean isPrefix(K prefix, int offsetInBits, int lengthInBits, K key);
}
