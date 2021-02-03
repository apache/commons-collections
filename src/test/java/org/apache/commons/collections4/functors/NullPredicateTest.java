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

import static org.apache.commons.collections4.functors.NullPredicate.nullPredicate;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

public class NullPredicateTest extends AbstractPredicateTest {
    @Test
    public void testNullPredicate() {
        assertSame(NullPredicate.nullPredicate(), NullPredicate.nullPredicate());
        assertPredicateTrue(nullPredicate(), null);
    }

    @Test
    public void ensurePredicateCanBeTypedWithoutWarning() throws Exception {
        final Predicate<String> predicate = NullPredicate.nullPredicate();
        assertPredicateFalse(predicate, cString);
    }

    @Override
    protected Predicate<?> generatePredicate() {
        return nullPredicate();
    }
}
