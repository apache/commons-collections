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
package org.apache.commons.collections4.splitmap;

import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * Unit tests for class {@link AbstractIterableGetMapDecorator}.
 *
 * @date 22.06.2017
 * @see AbstractIterableGetMapDecorator
 **/
public class AbstractIterableGetMapDecoratorTest {


    @Test
    public void testEquals() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        AbstractIterableGetMapDecorator<String, Object> abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<String, Object>(hashMap);

        assertTrue(abstractIterableGetMapDecorator.equals(abstractIterableGetMapDecorator.map));

        assertEquals(0, abstractIterableGetMapDecorator.size());

        assertTrue(abstractIterableGetMapDecorator.isEmpty());

    }


    @Test
    public void testIsEmptyReturningTrue() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        AbstractIterableGetMapDecorator<String, Object> abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<String, Object>(hashMap);

        assertTrue(abstractIterableGetMapDecorator.isEmpty());
        assertTrue(hashMap.isEmpty());

        assertEquals(0, abstractIterableGetMapDecorator.size());

        assertTrue(abstractIterableGetMapDecorator.isEmpty());

    }


    @Test
    public void testIsEmptyReturningFalse() {

        AbstractIterableGetMapDecorator<String, Object> abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<String, Object>();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        abstractIterableGetMapDecorator.map = (Map<String, Object>) hashMap;
        hashMap.put("D;{ A5j\"EQ",  null);

        assertFalse(abstractIterableGetMapDecorator.isEmpty());
        assertEquals(1, abstractIterableGetMapDecorator.size());

        //Again because we are paranoid.
        //At least a little bit :-)
        assertFalse(abstractIterableGetMapDecorator.isEmpty());

    }


    @Test(expected = NullPointerException.class)
    public void testFailsToCreateAbstractIterableGetMapDecoratorTakingMapThrowsNullPointerException() {

        AbstractIterableGetMapDecorator<Object, Object> abstractIterableGetMapDecorator = null;

        abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<Object, Object>(null);

    }


    @Test(expected = NullPointerException.class)  //I consider this to be a defect. Especially if the two constructors are compared.
    public void testCreatesAbstractIterableGetMapDecoratorTakingNoArguments() {

        AbstractIterableGetMapDecorator<String, Object> abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<String, Object>();
        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

        hashMap.put(abstractIterableGetMapDecorator, abstractIterableGetMapDecorator);

    }


    @Test
    public void testValues() {

        HashMap<Object, String> hashMap = new HashMap<Object, String>();
        AbstractIterableGetMapDecorator<Object, String> abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<Object, String>(hashMap);
        Collection<String> collection = abstractIterableGetMapDecorator.values();

        assertEquals(0, abstractIterableGetMapDecorator.size());
        assertTrue(abstractIterableGetMapDecorator.isEmpty());

    }


    @Test
    public void testKeySet() {

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        AbstractIterableGetMapDecorator<String, Object> abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<String, Object>(hashMap);
        Set<String> set = abstractIterableGetMapDecorator.keySet();

        assertTrue(abstractIterableGetMapDecorator.isEmpty());
        assertEquals(0, abstractIterableGetMapDecorator.size());

    }


    @Test(expected = NullPointerException.class)  //I consider this to be a defect. Especially if the two constructors are compared.
    public void testToStringThrowsNullPointerException() {

        AbstractIterableGetMapDecorator<String, Object> abstractIterableGetMapDecorator = new AbstractIterableGetMapDecorator<String, Object>();

        abstractIterableGetMapDecorator.toString();

    }


}