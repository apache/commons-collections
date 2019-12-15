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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.Objects;
import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.Signedness;
import org.junit.Test;

public class ShapeTest {

    HashFunctionIdentity testFunction = new HashFunctionIdentity() {

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

        /*
         * values from https://hur.st/bloomfilter/?n=5&p=.1&m=&k=
         *
         * n = 5
         *
         * p = 0.100375138 (1 in 10)
         *
         * m = 24 (3B)
         *
         * k = 3
         */


    Shape shape = new Shape(testFunction, 5, 0.1);


    @Test
    public void constructor_np_noName() {

        try {
            new Shape(null, 5, 0.1);
            fail( "Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException expected)
        {
            // do nothing
        }
    }

    @Test
    public void constructor_nm_noName() {

        try {
            new Shape(null, 5, 72);
            fail( "Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException expected)
        {
            // do nothing
        }
    }

    @Test
    public void constructor_nmk_noName() {

        try {
            new Shape(null, 5, 72, 17);
            fail( "Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException expected)
        {
            // do nothing
        }
    }

    @Test
    public void constructor_pmk_noName() {

        try {
            new Shape(null, 0.1, 72, 17);
            fail( "Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException expected)
        {
            // do nothing
        }
    }

    @Test
    public void constructor_items_probability_Test() {

        assertEquals(24, shape.getNumberOfBits());
        assertEquals(3, shape.getNumberOfBytes());
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(5, shape.getNumberOfItems());
        assertEquals(0.100375138, shape.getProbability(), 0.000001);

    }

    @Test
    public void constructor_items_probability_NumberOfBitsOverflowTest() {
        try {
            new Shape( testFunction, Integer.MAX_VALUE, 1.0 / 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructor_items_probability_BadNumberOfItemsTest() {
        try {
            new Shape( testFunction, 0, 1.0 / 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructor_items_probability_BadProbabilityTest() {
        try {
            new Shape(testFunction, 10, 0.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }

        try {
            new Shape(testFunction, 10, 1.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructor_items_bitsTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24
         */
        Shape filterConfig = new Shape(testFunction, 5, 24);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);

    }

    @Test
    public void constructor_items_bits_BadNumberOfItemsTest() {
        try {
            new Shape(testFunction, 0, 24);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_BadNumberOfBitsTest() {
        try {
            new Shape(testFunction, 5, 6);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_BadNumberOfHashFunctionsTest() {
        try {
            new Shape(testFunction, 16,8);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_hashTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24&k=4
         */
        Shape filterConfig = new Shape(testFunction, 5, 24, 4);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(4, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.102194782, filterConfig.getProbability(), 0.000001);

    }

    @Test
    public void constructor_items_bits_hash_BadNumberOfItemsTest() {
        try {
            new Shape(testFunction, 0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_hash_BadNumberOfBitsTest() {
        try {
            new Shape(testFunction, 5, 6, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_items_bits_hash_BadNumberOfHashFunctionsTest() {
        try {
            new Shape(testFunction, 5, 24, 0);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }


    @Test
    public void constructor_items_bits_hash_BadProbabilityTest() {
        try {
            new Shape(testFunction, 4000,8,1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_probability_bits_hashTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&p=.1&m=&k=
         */
        Shape filterConfig = new Shape(testFunction, 0.1, 24, 3);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);
    }

    @Test
    public void constructor__probability_bits_hash_BadProbabilityTest() {
        try {
            new Shape(testFunction, 0.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }

        try {
            new Shape(testFunction, -1.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
        try {
            new Shape(testFunction, 1.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }

        try {
            new Shape(testFunction, 2.0, 24, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor__probability_bits_hash__BadNumberOfBitsTest() {
        try {
            new Shape(testFunction, 0.5, 6, 1);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void constructor_probability_bits_hash_BadNumberOfHashFunctionsTest() {
        try {
            new Shape(testFunction, 0.5, 24, 0);
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected)
        {
            //expected
        }
    }

    @Test
    public void equalsTest() {

        assertEquals(new Shape(testFunction, 5, 1.0 / 10), shape);
        assertNotEquals(new Shape(testFunction, 5, 1.0 / 11), shape);
        assertNotEquals(new Shape(testFunction, 4, 1.0 / 10), shape);
        assertFalse(shape.equals(shape.toString()));

    }

    @Test
    public void hashCodeTest() {
        int hashCode = Objects.hash(testFunction, 24, 3 );
        assertEquals(hashCode, shape.hashCode());
    }


}
