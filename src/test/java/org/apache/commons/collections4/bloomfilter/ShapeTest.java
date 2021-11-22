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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@link Shape} class.
 */
public class ShapeTest {

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

    private final Shape shape = new Shape(3, 24);

    /**
     * Tests that if the number of bits less than 1 an IllegalArgumentException is thrown.
     */
    @Test
    public void constructor_items_bits_BadNumberOfBitsTest() {
        try {
            new Shape(5, 0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    // /**
    // * Tests that if the number of hash functions is less than 1 an
    // IllegalArgumentException is thrown.
    // */
    // @Test
    // public void constructor_items_bits_BadNumberOfHashFunctionsTest() {
    // try {
    // new Shape( 16, 8);
    //
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }

    // /**
    // * Tests that if the number of items less than 1 an IllegalArgumentException
    // is thrown.
    // */
    // @Test
    // public void constructor_items_bits_BadNumberOfItemsTest() {
    // try {
    // new Shape(testFunction, 0, 24);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }

    // /**
    // * Tests that if the number of bits is less than 1 an exception is thrown
    // */
    // @Test
    // public void constructor_items_bits_hash_BadNumberOfBitsTest() {
    // try {
    // new Shape(testFunction, 5, 0, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }

    /**
     * Tests that if the number of hash functions is less than 1 an exception is thrown.
     */
    @Test
    public void constructor_items_bits_hash_BadNumberOfHashFunctionsTest() {
        try {
            new Shape(0, 5);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    // /**
    // * Tests that if the number of items is less than 1 an exception is thrown.
    // */
    // @Test
    // public void constructor_items_bits_hash_BadNumberOfItemsTest() {
    // try {
    // new Shape(testFunction, 0, 24, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }

    // /**
    // * Tests that if the calculated probability is greater than or equal to 1 an
    // IllegalArgumentException is thrown
    // */
    // @Test
    // public void constructor_items_bits_hash_BadProbabilityTest() {
    // try {
    // new Shape(testFunction, 4000, 8, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }

    // /**
    // * Tests that when the number of items, number of bits and number of hash
    // functions is passed the values are
    // * calculated correctly.
    // */
    // @Test
    // public void constructor_items_bits_hashTest() {
    // /*
    // * values from https://hur.st/bloomfilter/?n=5&m=24&k=4
    // */
    // final Shape filterConfig = new Shape(testFunction, 5, 24, 4);
    //
    // assertEquals(24, filterConfig.getNumberOfBits());
    // assertEquals(4, filterConfig.getNumberOfHashFunctions());
    // assertEquals(5, filterConfig.getNumberOfItems());
    // assertEquals(0.102194782, filterConfig.getProbability(), 0.000001);
    // }

    // /**
    // * Tests that the number of items and number of bits is passed the other
    // values are calculated correctly.
    // */
    // @Test
    // public void constructor_items_bitsTest() {
    // /*
    // * values from https://hur.st/bloomfilter/?n=5&m=24
    // */
    // final Shape filterConfig = new Shape(testFunction, 5, 24);
    //
    // assertEquals(24, filterConfig.getNumberOfBits());
    // assertEquals(3, filterConfig.getNumberOfHashFunctions());
    // assertEquals(5, filterConfig.getNumberOfItems());
    // assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);
    // }
    //
    // /**
    // * Tests that if the number of items is less than 1 an
    // IllegalArgumentException is thrown.
    // */
    // @Test
    // public void constructor_items_probability_BadNumberOfItemsTest() {
    // try {
    // new Shape(testFunction, 0, 1.0 / 10);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // do nothing.
    // }
    // }
    //
    // /**
    // * Tests that if the probability is less than or equal to 0 or more than or
    // equal to 1 an IllegalArgumentException is thrown.
    // */
    // @Test
    // public void constructor_items_probability_BadProbabilityTest() {
    // try {
    // new Shape(testFunction, 10, 0.0);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // do nothing.
    // }
    // try {
    // new Shape(testFunction, 10, 1.0);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // do nothing.
    // }
    // try {
    // new Shape(testFunction, 10, Double.NaN);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // do nothing.
    // }
    // }
    //
    // /**
    // * Tests that if calculated number of bits is greater than Integer.MAX_VALUE
    // an IllegalArgumentException is thrown.
    // */
    // @Test
    // public void constructor_items_probability_NumberOfBitsOverflowTest() {
    // try {
    // new Shape(testFunction, Integer.MAX_VALUE, 1.0 / 10);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // do nothing.
    // }
    // }
    //
    // /**
    // * Tests the the probability is calculated correctly.
    // */
    // @Test
    // public void constructor_items_probability_Test() {
    //
    // assertEquals(24, shape.getNumberOfBits());
    // assertEquals(3, shape.getNumberOfHashFunctions());
    // assertEquals(5, shape.getNumberOfItems());
    // assertEquals(0.100375138, shape.getProbability(), 0.000001);
    // }
    //
    // /**
    // * Tests that the constructor with a null name, number of items and size of
    // filter fails.
    // */
    // @Test
    // public void constructor_nm_noName() {
    // try {
    // new Shape(null, 5, 72);
    // fail("Should throw NullPointerException");
    // } catch (final NullPointerException expected) {
    // // do nothing
    // }
    // }
    //
    // /**
    // * Tests that the constructor with a null name, number of items, size of
    // filter, and number of functions fails.
    // */
    // @Test
    // public void constructor_nmk_noName() {
    // try {
    // new Shape(null, 5, 72, 17);
    // fail("Should throw NullPointerException");
    // } catch (final NullPointerException expected) {
    // // do nothing
    // }
    // }
    //
    // /**
    // * Tests that the constructor with a null name, number of items, and
    // probability fails.
    // */
    // @Test
    // public void constructor_np_noName() {
    // try {
    // new Shape(null, 5, 0.1);
    // fail("Should throw NullPointerException");
    // } catch (final NullPointerException expected) {
    // // do nothing
    // }
    // }
    //
    // /**
    // * Tests that the constructor with a null name, probability, size of filter,
    // and number of functions fails.
    // */
    // @Test
    // public void constructor_pmk_noName() {
    // try {
    // new Shape(null, 0.1, 72, 17);
    // fail("Should throw NullPointerException");
    // } catch (final NullPointerException expected) {
    // // do nothing
    // }
    // }
    //
    // /**
    // * Tests that if the number of bits is less than 1 an exception is thrown
    // */
    // @Test
    // public void constructor_probability_bits_hash_BadNumberOfBitsTest() {
    // try {
    // new Shape(testFunction, 0.5, 0, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }
    //
    // /**
    // * Tests that if the number of functions is less than 1 an exception is thrown
    // */
    // @Test
    // public void constructor_probability_bits_hash_BadNumberOfHashFunctionsTest()
    // {
    // try {
    // new Shape(testFunction, 0.5, 24, 0);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }
    //
    // /**
    // * Tests that invalid probability values cause and IllegalArgumentException to
    // be thrown.
    // */
    // @Test
    // public void constructor_probability_bits_hash_BadProbabilityTest() {
    // // probability should not be 0
    // try {
    // new Shape(testFunction, 0.0, 24, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    //
    // // probability should not be = -1
    // try {
    // new Shape(testFunction, -1.0, 24, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    //
    // // probability should not be < -1
    // try {
    // new Shape(testFunction, -1.5, 24, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    //
    // // probability should not be = 1
    // try {
    // new Shape(testFunction, 1.0, 24, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    //
    // // probability should not be > 1
    // try {
    // new Shape(testFunction, 2.0, 24, 1);
    // fail("Should have thrown IllegalArgumentException");
    // } catch (final IllegalArgumentException expected) {
    // // expected
    // }
    // }
    //
    // /**
    // * Tests the calculated values of calling the constructor with the
    // probability, number of bits and number of hash
    // * functions.
    // */
    // @Test
    // public void constructor_probability_bits_hashTest() {
    // /*
    // * values from https://hur.st/bloomfilter/?n=5&p=.1&m=24&k=3
    // */
    // final Shape filterConfig = new Shape(testFunction, 0.1, 24, 3);
    //
    // assertEquals(24, filterConfig.getNumberOfBits());
    // assertEquals(3, filterConfig.getNumberOfHashFunctions());
    // assertEquals(5, filterConfig.getNumberOfItems());
    // assertEquals(0.100375138, filterConfig.getProbability(), 0.000001);
    // }
    //
    /**
     * Test equality of shape.
     */
    @Test
    public void equalsTest() {

        assertEquals(shape, shape);
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(24, shape.getNumberOfBits());
        assertEquals(shape.hashCode(), new Shape(3, 24).hashCode());
        assertNotEquals(shape, null);
        assertNotEquals(shape, new Shape(3, 25));
        assertNotEquals(shape, new Shape(4, 24));
    }

    @Test
    public void estimateNTest() {
        double[] expected = { 0.0, 0.3404769153503671, 0.6960910159170385, 1.068251140996181, 1.4585724543516367,
            1.8689188094520417, 2.301456579614247, 2.758723890333837, 3.243720864865314, 3.7600290339658846,
            4.311972005861497, 4.90483578309127, 5.545177444479562, 6.2412684603966, 7.003749898831201,
            7.8466340240938095, 8.788898309344876, 9.85714945034106, 11.090354888959125, 12.54892734331076,
            14.334075753824441, 16.635532333438686, 19.879253198304, 25.424430642783573 };
        for (int i = 0; i < 24; i++) {
            assertEquals(expected[i], shape.estimateN(i), 0.00000000000000001);
        }
    }

    @Test
    public void getProbabilityTest() {
        double[] expected = { 0.0, 0.0016223626694561954, 0.010823077182670957, 0.030579354491777785,
            0.06091618422799686, 0.1003751381786711, 0.14689159766038104, 0.19829601428155866, 0.25258045782764715,
            0.3080221532988778, 0.3632228594351169, 0.4171013016177174, 0.4688617281200601, 0.5179525036637239,
            0.5640228015164387, 0.6068817738972262, 0.6464623147796981, 0.6827901771310362, 0.7159584363083427,
            0.7461068849672469, 0.7734057607554121, 0.7980431551369204, 0.8202154721379679, 0.8401203636727712 };
        for (int i = 0; i < 24; i++) {
            assertEquals(expected[i], shape.getProbability(i), 0.00000000000000001);
        }
    }

}
