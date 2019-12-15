/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.PrimitiveIterator.OfInt;
import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.Signedness;
import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.apache.commons.collections4.bloomfilter.Hasher;
import org.junit.Before;
import org.junit.Test;

public class DynamicHasherTest {
    private DynamicHasher.Builder builder;
    private Shape shape;

    private HashFunctionIdentity testFunction = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test Function";
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }

        @Override
        public ProcessType getProcess() {
            return ProcessType.CYCLIC;
        }

        @Override
        public long getSignature() {
            return 0;
        }};

    @Before
    public void setup() throws NoSuchAlgorithmException {
        builder = new DynamicHasher.Builder( new MD5Cyclic() );
        shape = new Shape(new MD5Cyclic(), 3, 72, 17);
    }

    @Test
    public void testGetBits() throws Exception {

        int[] expected = {6, 69, 44, 19, 10, 57, 48, 23, 70, 61, 36, 11, 2, 49, 24, 15, 62};

        Hasher hasher = builder.with("Hello").build();

        OfInt iter = hasher.getBits(shape);

        for (int i = 0; i < expected.length; i++) {
            assertTrue(iter.hasNext());
            assertEquals(expected[i], iter.nextInt());
        }
        assertFalse(iter.hasNext());

    }

    @Test
    public void testGetBits_MultipleHashes() throws Exception {
        int[] expected = {6, 69, 44, 19, 10, 57, 48, 23, 70, 61, 36, 11, 2, 49, 24, 15, 62, 1, 63, 53, 43, 17, 7, 69,
            59, 49, 39, 13, 3, 65, 55, 45, 35, 25};

        Hasher hasher = builder.with("Hello").with("World").build();

        OfInt iter = hasher.getBits(shape);

        for (int i = 0; i < expected.length; i++) {
            assertTrue(iter.hasNext());
            assertEquals(expected[i], iter.nextInt());
        }
        assertFalse(iter.hasNext());

    }

    @Test
    public void testGetBits_WongShape() throws Exception {

         Hasher hasher = builder.with("Hello").build();

       try {
            hasher.getBits(new Shape( testFunction, 3, 72, 17) );
            fail( "Should have thown IllegalArgumentException");
          }
          catch (IllegalArgumentException expected) {
              // do nothing
          }
    }

}
