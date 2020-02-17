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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.Objects;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.junit.Test;

/**
 * Tests that the Shap class.
 *
 */
public class ShapeTest {

    private final HashFunctionIdentity testFunction = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test Function";
        }

        @Override
        public ProcessType getProcessType() {
            return ProcessType.CYCLIC;
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public long getSignature() {
            return 0;
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }
    };

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

    private final Shape shape = new Shape(testFunction, 5, 0.1);

    /**
     * Tests that if the number of bits less than 8 an IllegalArgumentException is thrown.
     */
    @Test
    public void constructor_items_bits_BadNumberOfBitsTest() {
        try {
            new Shape(testFunction, 5, 6);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of hash functions is less than 1 an exception is thrown.
     */
    @Test
    public void constructor_items_bits_BadNumberOfHashFunctionsTest() {
        try {
            new Shape(testFunction, 16, 8);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of items less than 1 an IllegalArgumentException is thrown.
     */
    @Test
    public void constructor_items_bits_BadNumberOfItemsTest() {
        try {
            new Shape(testFunction, 0, 24);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of bits is less than 8 an exception is thrown
     */
    @Test
    public void constructor_items_bits_hash_BadNumberOfBitsTest() {
        try {
            new Shape(testFunction, 5, 6, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of hash functions is less than 1 an exception is thrown.
     */
    @Test
    public void constructor_items_bits_hash_BadNumberOfHashFunctionsTest() {
        try {
            new Shape(testFunction, 5, 24, 0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of items is less than 1 an exception is thrown.
     */
    @Test
    public void constructor_items_bits_hash_BadNumberOfItemsTest() {
        try {
            new Shape(testFunction, 0, 24, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the calculated probability is greater than or equal to 1 an IllegalArgumentException is thrown
     */
    @Test
    public void constructor_items_bits_hash_BadProbabilityTest() {
        try {
            new Shape(testFunction, 4000, 8, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that when the number of items, number of bits and number of hash functions is passed the values are
     * calculated correctly.
     */
    @Test
    public void constructor_items_bits_hashTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24&k=4
         */
        final Shape filterConfig = new Shape(testFunction, 5, 24, 4);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(4, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.102194782, filterConfig.getProbability(), 0.000001);
    }

    /**
     * Tests that the number of items and number of bits is passed the other values are calculated correctly.
     */
    @Test
    public void constructor_items_bitsTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24
         */
        final Shape filterConfig = new Shape(testFunction, 5, 24);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);
    }

    /**
     * Tests that if the number of items is less than 1 an IllegalArgumentException is thrown.
     */
    @Test
    public void constructor_items_probability_BadNumberOfItemsTest() {
        try {
            new Shape(testFunction, 0, 1.0 / 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests that if the probability is less than or equal to 0 an IllegalArgumentException is thrown.
     */
    @Test
    public void constructor_items_probability_BadProbabilityTest() {
        try {
            new Shape(testFunction, 10, 0.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }

        try {
            new Shape(testFunction, 10, 1.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests that if calculated number of bits is greater than Integer.MAX_VALUE an IllegalArgumentException is thrown.
     */
    @Test
    public void constructor_items_probability_NumberOfBitsOverflowTest() {
        try {
            new Shape(testFunction, Integer.MAX_VALUE, 1.0 / 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests the the probability is calculated correctly.
     */
    @Test
    public void constructor_items_probability_Test() {

        assertEquals(24, shape.getNumberOfBits());
        assertEquals(3, shape.getNumberOfBytes());
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(5, shape.getNumberOfItems());
        assertEquals(0.100375138, shape.getProbability(), 0.000001);
    }

    /**
     * Tests that the constructor with a null name, number of items and size of filter fails.
     */
    @Test
    public void constructor_nm_noName() {

        try {
            new Shape(null, 5, 72);
            fail("Should throw NullPointerException");
        } catch (final NullPointerException expected) {
            // do nothing
        }
    }

    /**
     * Tests that the constructor with a null name, number of items, size of filter, and number of functions fails.
     */
    @Test
    public void constructor_nmk_noName() {

        try {
            new Shape(null, 5, 72, 17);
            fail("Should throw NullPointerException");
        } catch (final NullPointerException expected) {
            // do nothing
        }
    }

    /**
     * Tests that the constructor with a null name, number of items, and probability fails.
     */
    @Test
    public void constructor_np_noName() {

        try {
            new Shape(null, 5, 0.1);
            fail("Should throw NullPointerException");
        } catch (final NullPointerException expected) {
            // do nothing
        }
    }

    /**
     * Tests that the constructor with a null name, probability, size of filter, and number of functions fails.
     */
    @Test
    public void constructor_pmk_noName() {

        try {
            new Shape(null, 0.1, 72, 17);
            fail("Should throw NullPointerException");
        } catch (final NullPointerException expected) {
            // do nothing
        }
    }

    /**
     * Tests that if the number of bits is less than 8 an exception is thrown
     */
    @Test
    public void constructor_probability_bits_hash__BadNumberOfBitsTest() {
        try {
            new Shape(testFunction, 0.5, 6, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of functions is less than 1 an exception is thrown
     */
    @Test
    public void constructor_probability_bits_hash_BadNumberOfHashFunctionsTest() {
        try {
            new Shape(testFunction, 0.5, 24, 0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that invalid probability values cause and IllegalArgumentException to be thrown.
     */
    @Test
    public void constructor_probability_bits_hash_BadProbabilityTest() {
        // probability should not be 0
        try {
            new Shape(testFunction, 0.0, 24, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }

        // probability should not be = -1
        try {
            new Shape(testFunction, -1.0, 24, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }

        // probability should not be < -1
        try {
            new Shape(testFunction, -1.5, 24, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }

        // probability should not be = 1
        try {
            new Shape(testFunction, 1.0, 24, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }

        // probability should not be > 1
        try {
            new Shape(testFunction, 2.0, 24, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests the calculated values of calling the constructor with the probability, number of bits and number of hash
     * functions.
     */
    @Test
    public void constructor_probability_bits_hashTest() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&p=.1&m=&k=
         */
        final Shape filterConfig = new Shape(testFunction, 0.1, 24, 3);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);
    }

    /**
     * Test equality of shape.
     */
    @Test
    public void equalsTest() {

        assertEquals(new Shape(testFunction, 5, 1.0 / 10), shape);
        assertNotEquals(new Shape(testFunction, 5, 1.0 / 11), shape);
        assertNotEquals(new Shape(testFunction, 4, 1.0 / 10), shape);

        final HashFunctionIdentity testFunction2 = new HashFunctionIdentity() {

            @Override
            public String getName() {
                return "Test Function2";
            }

            @Override
            public ProcessType getProcessType() {
                return ProcessType.CYCLIC;
            }

            @Override
            public String getProvider() {
                return "Apache Commons Collection Tests";
            }

            @Override
            public long getSignature() {
                return 0;
            }

            @Override
            public Signedness getSignedness() {
                return Signedness.SIGNED;
            }
        };

        assertNotEquals(new Shape(testFunction2, 4, 1.0 / 10), shape);
    }

    /**
     * Test that hashCode equals hashCode of hashFunctionIdentity
     */
    @Test
    public void hashCodeTest() {
        final int hashCode = Objects.hash(testFunction, 24, 3);
        assertEquals(hashCode, shape.hashCode());
    }
}
