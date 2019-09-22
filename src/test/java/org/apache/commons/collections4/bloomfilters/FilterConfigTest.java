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
package org.apache.commons.collections4.bloomfilters;

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
    FilterConfig filterConfig = new FilterConfig(5, 10);

    @Test
    public void constructorTest() {

        assertEquals(24, filterConfig.getNumberOfBits());
        assertEquals(3, filterConfig.getNumberOfBytes());
        assertEquals(3, filterConfig.getNumberOfHashFunctions());
        assertEquals(5, filterConfig.getNumberOfItems());
        assertEquals(10, filterConfig.getProbability());

    }

    @Test
    public void constructorOverflowTest() {
        try {
            new FilterConfig(Integer.MAX_VALUE, 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructorBadNumberOfItemsTest() {
        try {
            new FilterConfig(0, 10);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void constructorBadProbabilityTest() {
        try {
            new FilterConfig(10, 0);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }
    }

    @Test
    public void serializerTest() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(filterConfig);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object o = ois.readObject();
            ois.close();
            assertTrue(o instanceof FilterConfig);
            FilterConfig fc = (FilterConfig) o;
            assertEquals(filterConfig.getNumberOfBits(), fc.getNumberOfBits());
            assertEquals(filterConfig.getNumberOfBytes(), fc.getNumberOfBytes());
            assertEquals(filterConfig.getNumberOfHashFunctions(), fc.getNumberOfHashFunctions());
            assertEquals(filterConfig.getNumberOfItems(), fc.getNumberOfItems());
            assertEquals(filterConfig.getProbability(), fc.getProbability());

        }

    }
    
    @Test
    public void equalsTest() {
        
        assertEquals( new FilterConfig(5, 10), filterConfig );
        assertNotEquals( new FilterConfig(5, 11), filterConfig );
        assertNotEquals( new FilterConfig(4, 10), filterConfig );
        assertFalse( filterConfig.equals( filterConfig.toString() ));
        
    }
    
    @Test
    public void hashCodeTest() {
        int hashCode = Objects.hash(24, 3, 5, 10 );
        
        assertEquals( hashCode, filterConfig.hashCode());
        // forces path throgh previously calcualted hashCode
        assertNotEquals( hashCode+1, filterConfig.hashCode());
           
    }
}
