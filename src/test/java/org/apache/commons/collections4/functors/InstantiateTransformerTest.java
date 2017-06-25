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

import org.junit.Test;

import java.lang.reflect.Array;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Unit tests for class {@link InstantiateTransformer}.
 *
 * @date 25.06.2017
 * @see InstantiateTransformer
 **/
public class InstantiateTransformerTest {


    @Test
    public void testTransformThrowsRuntimeException() {

        InstantiateTransformer<Object> instantiateTransformer = new InstantiateTransformer<Object>(null,null);

        try {
            instantiateTransformer.transform((Class<?>) null);
            fail("Expecting exception: RuntimeException");
        } catch(RuntimeException e) {
            assertEquals("InstantiateTransformer: Input object was not an instanceof Class, it was a null object",e.getMessage());
            assertEquals(InstantiateTransformer.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testInstantiateTransformerWithEmptyArray() {

        Class<String>[] classArray = (Class<String>[]) Array.newInstance(Class.class, 0);

        assertNotNull( InstantiateTransformer.instantiateTransformer(classArray,classArray) );

    }


    @Test
    public void testInstantiateTransformerThrowsIllegalArgumentException() {

        Class<Object>[] classArray = (Class<Object>[]) Array.newInstance(Class.class, 0);

        try {
            InstantiateTransformer.instantiateTransformer((Class<?>[]) classArray, (Object[]) null);
            fail("Expecting exception: IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            assertEquals("Parameter types must match the arguments",e.getMessage());
            assertEquals(InstantiateTransformer.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testInstantiateTransformerWithNull() {

        assertNotNull( InstantiateTransformer.instantiateTransformer(null,null) );

    }


}