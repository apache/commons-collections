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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import org.apache.commons.collections4.bloomfilter.FilterConfiguration;
import org.apache.commons.collections4.bloomfilter.FilterConfiguration;
import org.junit.Test;

public class FilterConfigTest {

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
    FilterConfiguration filterConfig = new FilterConfiguration(5, 1.0/10);

    @Test
    public void constructorTest() {

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(1.0/10, filterConfig.getProbability(), 0.000001);

    }

    @Test
    public void constructorOverflowTest() {
        try {
            new FilterConfiguration(Integer.MAX_VALUE, 1.0/10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructorBadNumberOfItemsTest() {
        try {
            new FilterConfiguration(0, 1.0/10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructorBadProbabilityTest() {
        try {
            new FilterConfiguration(10, 0.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
        
        try {
            new FilterConfiguration(10, 1.0);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }
    
    @Test
    public void equalsTest() {
        
        assertEquals( new FilterConfiguration(5, 1.0/10), filterConfig );
        assertNotEquals( new FilterConfiguration(5, 1.0/11), filterConfig );
        assertNotEquals( new FilterConfiguration(4, 1.0/10), filterConfig );
        assertFalse( filterConfig.equals( filterConfig.toString() ));
        
    }
    
    @Test
    public void hashCodeTest() {
        int hashCode = Objects.hash(24, 3, 5, 1.0/10 );
        
        assertEquals( hashCode, filterConfig.hashCode());
        // forces path throgh previously calcualted hashCode
        assertNotEquals( hashCode+1, filterConfig.hashCode());
           
    }
}
