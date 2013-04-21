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
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Test class for PermutationIterator.
 *
 * @version $Id$
 * @since 4.0
 */
public class PermutationIteratorTest extends AbstractIteratorTest<List<Character>> {

    protected Character[] testArray = { 'A', 'B', 'C' };
    protected List<Character> testList;

    public PermutationIteratorTest(final String testName) {
        super(testName);
    }

    @Override
    public void setUp() {
        testList = new ArrayList<Character>();
        testList.addAll(Arrays.asList(testArray));
    }

    //-----------------------------------------------------------------------
    
    public boolean supportsRemove() {
        return false;
    }
    
    public boolean supportsEmptyIterator() {
        return false;
    }

    @Override
    public PermutationIterator<Character> makeEmptyIterator() {
        return new PermutationIterator<Character>(new ArrayList<Character>());
    }

    @Override
    public PermutationIterator<Character> makeObject() {
        return new PermutationIterator<Character>(testList);
    }

    //-----------------------------------------------------------------------

    public void testPermutationResultSize() {
        int factorial = 1;
        for (int i = 0; i < 8; i++, factorial*=i) {
            List<Integer> list = new ArrayList<Integer>();
            for (int j = 0; j < i; j++) {
                list.add(j);
            }
            Iterator<List<Integer>> it = new PermutationIterator<Integer>(list);
            int count = 0;
            while (it.hasNext()) {
                it.next();
                count++;
            }
            assertEquals(factorial, count);
        }
    }

    /**
     * test checking that all the permutations are returned
     */
    public void testPermutationExhaustivity() {
        List<Character> perm1 = new ArrayList<Character>();
        List<Character> perm2 = new ArrayList<Character>();
        List<Character> perm3 = new ArrayList<Character>();
        List<Character> perm4 = new ArrayList<Character>();
        List<Character> perm5 = new ArrayList<Character>();
        List<Character> perm6 = new ArrayList<Character>();

        perm1.add('A');
        perm2.add('A');
        perm3.add('B');
        perm4.add('B');
        perm5.add('C');
        perm6.add('C');

        perm1.add('B');
        perm2.add('C');
        perm3.add('A');
        perm4.add('C');
        perm5.add('A');
        perm6.add('B');

        perm1.add('C');
        perm2.add('B');
        perm3.add('C');
        perm4.add('A');
        perm5.add('B');
        perm6.add('A');

        List<List<Character>> results = new ArrayList<List<Character>>();
        
        PermutationIterator<Character> it = makeObject();
        while (it.hasNext()) {
            List<Character> next = it.next();
            results.add(next);
        }
        //3! permutation for 3 elements
        assertEquals(6, results.size());
        assertTrue(results.contains(perm1));
        assertTrue(results.contains(perm2));
        assertTrue(results.contains(perm3));
        assertTrue(results.contains(perm4));
        assertTrue(results.contains(perm5));
        assertTrue(results.contains(perm6));
    }

    /**
     * test checking that all the permutations are returned only once.
     */    
    public void testPermutationUnicity() {
        List<List<Character>> resultsList = new ArrayList<List<Character>>();
        Set<List<Character>> resultsSet = new HashSet<List<Character>>();
        
        PermutationIterator<Character> it = makeObject();
        while (it.hasNext()) {
            List<Character> permutation = it.next();
            resultsList.add(permutation);
            resultsSet.add(permutation);
        }
        //3! permutation for 3 elements
        assertEquals(6, resultsList.size());
        assertEquals(6, resultsSet.size());
    }

    public void testPermutationException() {
        List<List<Character>> resultsList = new ArrayList<List<Character>>();
        
        PermutationIterator<Character> it = makeObject();
        while (it.hasNext()) {
            List<Character> permutation = it.next();
            resultsList.add(permutation);
        }
        //asking for another permutation should throw an exception
        try {
            it.next();
            fail();
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void testPermutatorHasMore() {
        PermutationIterator<Character> it = makeObject();
        for (int i = 0; i < 6; i++) {
            assertTrue(it.hasNext());
            it.next();
        }
        assertFalse(it.hasNext());
    }

    public void testEmptyCollection() {
        PermutationIterator<Character> it = makeEmptyIterator();
        // there is one permutation for an empty set: 0! = 1
        assertTrue(it.hasNext());
        
        List<Character> nextPermutation = it.next();
        assertEquals(0, nextPermutation.size());
        
        assertFalse(it.hasNext());
    }
}