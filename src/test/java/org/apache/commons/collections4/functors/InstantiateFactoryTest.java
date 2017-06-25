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
package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Factory;
import org.junit.Test;

import java.lang.reflect.Array;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Unit tests for class {@link InstantiateFactory}.
 *
 * @date 25.06.2017
 * @see InstantiateFactory
 **/
public class InstantiateFactoryTest {


    @Test
    public void testInstantiateFactory() {

        Class<Object> clasz = Object.class;
        Class<String>[] classArray = (Class<String>[]) Array.newInstance(Class.class, 0);
        Factory<Object> factory = InstantiateFactory.instantiateFactory(clasz, (Class<?>[]) classArray, (Object[]) classArray);

        assertNotNull(factory);

    }


    @Test
    public void testInstantiateFactoryWithNonEmptyArray() {

        Class<String> clasz = String.class;
        Class<Object>[] classArray = (Class<Object>[]) Array.newInstance(Class.class, 1);
        Object[] objectArray = new Object[2];

        try {
            InstantiateFactory.instantiateFactory(clasz, (Class<?>[]) classArray, objectArray);
            fail("Expecting exception: IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            assertEquals("Parameter types must match the arguments",e.getMessage());
            assertEquals(InstantiateFactory.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testInstantiateFactoryWithNull() {

        Class<Object> clasz = Object.class;
        Class<String>[] classArray = (Class<String>[]) Array.newInstance(Class.class, 4);

        try {
            InstantiateFactory.instantiateFactory(clasz, (Class<?>[]) classArray, (Object[]) null);
            fail("Expecting exception: IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            assertEquals("Parameter types must match the arguments",e.getMessage());
            assertEquals(InstantiateFactory.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


}