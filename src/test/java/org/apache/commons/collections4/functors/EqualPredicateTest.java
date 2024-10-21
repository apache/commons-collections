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

import static org.junit.jupiter.api.Assertions.assertSame;

import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

public class EqualPredicateTest extends AbstractPredicateTest {
    public static class EqualsTestObject {
        private final boolean b;

        public EqualsTestObject(final boolean b) {
            this.b = b;
        }

        @Override
        public boolean equals(final Object obj) {
            return b;
        }
    }

    private static final EqualsTestObject FALSE_OBJECT = new EqualsTestObject(false);

    private static final EqualsTestObject TRUE_OBJECT = new EqualsTestObject(true);

    @Override
    protected Predicate<Object> generatePredicate() {
        return EqualPredicate.equalPredicate(null);
    }

    @Test
    public void testNullArgumentEqualsNullPredicate() throws Exception {
        assertSame(NullPredicate.nullPredicate(), EqualPredicate.equalPredicate(null));
    }

    @Test
    public void testObjectFactoryUsesEqualsForTest() throws Exception {
        final Predicate<EqualsTestObject> predicate = EqualPredicate.equalPredicate(FALSE_OBJECT);
        assertPredicateFalse(predicate, FALSE_OBJECT);
        assertPredicateTrue(EqualPredicate.equalPredicate(TRUE_OBJECT), TRUE_OBJECT);
    }

    @SuppressWarnings("boxing")
    @Test
    public void testPredicateTypeCanBeSuperClassOfObject() throws Exception {
        final Predicate<Number> predicate = EqualPredicate.equalPredicate((Number) 4);
        assertPredicateTrue(predicate, 4);
    }
}
