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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;


/**
 * Unit tests for class {@link InstanceofPredicate}.
 *
 * @date 25.06.2017
 * @see InstanceofPredicate
 **/
public class InstanceofPredicateTest {


    @Test
    public void testInstanceOfPredicateThrowsNullPointerException() {

        try {
            InstanceofPredicate.instanceOfPredicate((Class<?>) null);
            fail("Expecting exception: NullPointerException");
        } catch(NullPointerException e) {
            assertEquals("The type to check instanceof must not be null",e.getMessage());
            assertEquals(InstanceofPredicate.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }


    @Test
    public void testCreatesInstanceofPredicateAndCallsGetType() {

        Class<String> clasz = String.class;
        InstanceofPredicate instanceofPredicate = new InstanceofPredicate(clasz);
        Class<?> type = instanceofPredicate.getType();

        assertEquals("class java.lang.String", type.toString());
        assertFalse(type.isAnnotation());

        assertFalse(type.isEnum());
        assertFalse(type.isSynthetic());

        assertFalse(type.isInterface());
        assertFalse(type.isArray());

        assertFalse(type.isPrimitive());

    }


}