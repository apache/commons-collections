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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Unit tests for class {@link ConstantTransformer}.
 *
 * @date 25.06.2017
 * @see ConstantTransformer
 **/
public class ConstantTransformerTest {


    @Test
    public void testEqualsReturningTrue() {

        Integer integer = new Integer(1);
        ConstantTransformer<Integer, Object> constantTransformer = new ConstantTransformer<Integer, Object>(integer);
        Integer integerTwo = new Integer(1);
        ConstantTransformer<Object, Integer> constantTransformerTwo = new ConstantTransformer<Object, Integer>(integerTwo);

        assertTrue(integer.equals(integerTwo));
        assertTrue(integerTwo.equals(integer));

        assertTrue(constantTransformerTwo.equals(constantTransformer));

    }


    @Test
    public void testEqualsReturningFalse() {

        ConstantTransformer<String, Object> constantTransformer =
                new ConstantTransformer<String, Object>("org.apache.commons.collections4.functors.IfClosure");
        ConstantTransformer<Object, String> constantTransformerTwo =
                new ConstantTransformer<Object, String>(null);

        assertFalse(constantTransformer.equals(constantTransformerTwo));

    }


    @Test
    public void testEqualsWithNonNull() {

        ConstantTransformer<Object, Object> constantTransformer =
                new ConstantTransformer<Object, Object>("The predicate and closure map must not be null");

        assertFalse(constantTransformer.equals("The predicate and closure map must not be null"));

    }


    @Test
    public void testEqualsWithSameObject() {

        ConstantTransformer<Object, Object> constantTransformer =
                new ConstantTransformer<Object, Object>("a");

        assertTrue(constantTransformer.equals(constantTransformer));

    }



}