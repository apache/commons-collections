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

import org.apache.commons.collections4.trie.analyzer.StringKeyAnalyzer;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Unit tests for class {@link KeyAnalyzer}.
 *
 * @date 22.06.2017
 * @see KeyAnalyzer
 **/
public class KeyAnalyzerTest {


    @Test
    public void testCompareReturningPositive() {

        StringKeyAnalyzer stringKeyAnalyzer = StringKeyAnalyzer.INSTANCE;

        assertEquals(1, stringKeyAnalyzer.compare("a", null));

    }


    @Test
    public void testCompareReturningZero() {

        StringKeyAnalyzer stringKeyAnalyzer = new StringKeyAnalyzer();

        assertEquals(0, stringKeyAnalyzer.compare(null, null));

    }


    @Test
    public void testIsValidBitIndex() {

        assertFalse(KeyAnalyzer.isValidBitIndex(-1350));

    }


    @Test
    public void testIsNullBitKeyReturningTrue() {

        assertTrue(KeyAnalyzer.isNullBitKey(-1));

    }


    @Test
    public void testIsNullBitKeyReturningFalse() {

        assertFalse(KeyAnalyzer.isNullBitKey(0));

    }


    @Test
    public void testIsEqualBitKeyReturningTrue() {

        assertTrue( KeyAnalyzer.isEqualBitKey(-2));

    }


    @Test
    public void testIsEqualBitKeyReturningFalse() {

        assertFalse(KeyAnalyzer.isEqualBitKey(-1));

    }


    @Test
    public void testIsOutOfBoundsIndex() {

        assertTrue(KeyAnalyzer.isOutOfBoundsIndex(-3));

    }


}