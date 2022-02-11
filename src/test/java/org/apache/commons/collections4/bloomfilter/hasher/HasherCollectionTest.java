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
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link HasherCollection}.
 */
public class HasherCollectionTest extends AbstractHasherTest {

    @Override
    protected HasherCollection createHasher() {
        return new HasherCollection(new SimpleHasher(1, 1), new SimpleHasher(2, 2));
    }

    @Override
    protected HasherCollection createEmptyHasher() {
        return new HasherCollection();
    }

    @Override
    @Test
    public void testSize() {
        HasherCollection hasher = createHasher();
        assertEquals(2, hasher.size());
        assertEquals(2, hasher.getHashers().size());
        hasher = createEmptyHasher();
        assertEquals(0, createEmptyHasher().size());
        assertEquals(0, createEmptyHasher().getHashers().size());
    }

    protected void nestedTest(HasherCollectionTest nestedTest) {
        nestedTest.testAsIndexArray();
        nestedTest.testForEachIndex();
        nestedTest.testIsEmpty();
        nestedTest.testSize();
        nestedTest.testAdd();
    }

    @Test
    public void testCollectionConstructor() {
        List<Hasher> lst = Arrays.asList(new SimpleHasher(3, 2), new SimpleHasher(4, 2));
        HasherCollectionTest nestedTest = new HasherCollectionTest() {
            @Override
            protected HasherCollection createHasher() {
                return new HasherCollection(lst);
            }

            @Override
            protected HasherCollection createEmptyHasher() {
                return new HasherCollection();
            }
        };
        nestedTest(nestedTest);

        nestedTest = new HasherCollectionTest() {
            @Override
            protected HasherCollection createHasher() {
                return new HasherCollection(new SimpleHasher(3, 2), new SimpleHasher(4, 2));
            }

            @Override
            protected HasherCollection createEmptyHasher() {
                return new HasherCollection();
            }
        };
        nestedTest(nestedTest);
    }

    @Test
    public void testAdd() {
        HasherCollection hasher = createHasher();
        hasher.add(new SimpleHasher(2, 2));
        assertEquals(3, hasher.size());

        hasher.add(Arrays.asList(new SimpleHasher(3, 2), new SimpleHasher(4, 2)));
        assertEquals(5, hasher.size());
    }


    @Override
    public void testUniqueIndex() {
        // create a hasher that produces duplicates with the specified shape.
        // this setup produces 5, 17, 29, 41, 53, 65 two times
        Shape shape = Shape.fromKM( 12, 72 );
        Hasher h1 = new SimpleHasher( 5, 12 );
        HasherCollection hasher = createEmptyHasher();
        hasher.add( h1 );
        hasher.add( h1 );
        List<Integer> lst = new ArrayList<>();
        for (int i : new int[] { 5,17, 29, 41, 53, 65}) {
            lst.add( i );
            lst.add( i );
        }

        assertTrue( hasher.uniqueIndices( shape ).forEachIndex( i -> { return lst.remove( Integer.valueOf( i ));}), "unable to remove value" );
        assertEquals( 0, lst.size() );

    }
}
