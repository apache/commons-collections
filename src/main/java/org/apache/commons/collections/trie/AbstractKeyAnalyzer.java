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
 * TODO: add javadoc
 * 
 * @since 4.0
 * @version $Id$
 */
public abstract class AbstractKeyAnalyzer<K> implements KeyAnalyzer<K> {
    
    private static final long serialVersionUID = -20497563720380683L;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int compare(final K o1, final K o2) {
        if (o1 == null) {
            return o2 == null ? 0 : -1;
        } else if (o2 == null) {
            return 1;
        }
        
        return ((Comparable<K>)o1).compareTo(o2);
    }
    
    /** 
     * Returns true if bitIndex is a {@link KeyAnalyzer#OUT_OF_BOUNDS_BIT_KEY}
     */
    static boolean isOutOfBoundsIndex(final int bitIndex) {
        return bitIndex == OUT_OF_BOUNDS_BIT_KEY;
    }

    /** 
     * Returns true if bitIndex is a {@link KeyAnalyzer#EQUAL_BIT_KEY}
     */
    static boolean isEqualBitKey(final int bitIndex) {
        return bitIndex == EQUAL_BIT_KEY;
    }

    /** 
     * Returns true if bitIndex is a {@link KeyAnalyzer#NULL_BIT_KEY} 
     */
    static boolean isNullBitKey(final int bitIndex) {
        return bitIndex == NULL_BIT_KEY;
    }

    /** 
     * Returns true if the given bitIndex is valid. Indices 
     * are considered valid if they're between 0 and 
     * {@link Integer#MAX_VALUE}
     */
    static boolean isValidBitIndex(final int bitIndex) {
        return 0 <= bitIndex && bitIndex <= Integer.MAX_VALUE;
    }
}
