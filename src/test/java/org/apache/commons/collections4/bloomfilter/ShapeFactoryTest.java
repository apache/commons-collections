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


import java.util.ArrayList;

import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link Shape} class.
 */
public class ShapeFactoryTest {


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

    private final Shape shape = new Shape(3, 24 );

    /**
     * Tests that if the number of items less than 1 an IllegalArgumentException is thrown.
     */
    @Test
    public void badNumberOfItemsTest() {
        try {
            Shape.Factory.fromNM(0, 24);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
        try {
            Shape.Factory.fromNMK(0, 24, 5);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
        try {
            Shape.Factory.fromNP(0, 0.02 );
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of bits is less than 1 an exception is thrown
     */
    @Test
    public void badNumberOfBitsTest() {
        try {
            Shape.Factory.fromNM( 5, 0 );
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
        try {
            Shape.Factory.fromNMK( 5, 0, 7 );
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
        try {
            Shape.Factory.fromPMK( 0.035, 0, 7 );
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the number of hash functions is less than 1 an exception is thrown.
     */
    @Test
    public void badNumberOfHashFunctionsTest() {
        try {
            Shape.Factory.fromNMK(5, 26, 0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
        try {
            Shape.Factory.fromPMK(0.35, 26, 0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Tests that if the calculated probability is greater than or equal to 1 an IllegalArgumentException is thrown
     */
    @Test
    public void badProbabilityTest() {
        try {
            Shape.Factory.fromNMK( 4000, 8, 1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
        try {
            Shape.Factory.fromNP(10, 0.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
        try {
            Shape.Factory.fromNP( 10, 1.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
        try {
            Shape.Factory.fromNP( 10, Double.NaN);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }


    /**
     * Tests that when the number of items, number of bits and number of hash functions is passed the values are
     * calculated correctly.
     */
    @Test
    public void fromNMK_test() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24&k=4
         */
        final Shape filterConfig = Shape.Factory.fromNMK( 5, 24, 4);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(4, filterConfig.getNumberOfHashFunctions());
        assertEquals(0.102194782, filterConfig.getProbability(5 ), 0.000001);
    }

    /**
     * Tests that the number of items and number of bits is passed the other values are calculated correctly.
     */
    @Test
    public void fromNM_Test() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24
         */
        final Shape filterConfig = Shape.Factory.fromNM(5, 24);

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(0.100375138, filterConfig.getProbability(5), 0.000001);
    }




    /**
     * Tests that if calculated number of bits is greater than Integer.MAX_VALUE an IllegalArgumentException is thrown.
     */
    @Test
    public void numberOfBitsOverflowTest() {
        try {
            Shape.Factory.fromNP(Integer.MAX_VALUE, 0.1);
            fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing.
        }
    }

    /**
     * Tests the the probability is calculated correctly.
     */
    @Test
    public void probabilityTest() {
        Shape shape = Shape.Factory.fromNMK(5, 24, 3 );
        assertEquals(24, shape.getNumberOfBits());
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(0.100375138, shape.getProbability(5), 0.000001);
    }




    /**
     * Tests the calculated values of calling the constructor with the probability, number of bits and number of hash
     * functions.
     */
    @Test
    public void fromPMK_test() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&p=.1&m=24&k=3
         */
        final Shape shape = Shape.Factory.fromPMK( 0.1, 24, 3);

        assertEquals(24, shape.getNumberOfBits());
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(0.100375138, shape.getProbability(5), 0.000001);
    }



}
