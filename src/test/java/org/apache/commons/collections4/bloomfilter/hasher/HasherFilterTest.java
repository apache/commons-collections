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
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests the {@link Hasher.Filter}.
 */
public class HasherFilterTest {

    @Test
    public void testBasicFiltering() {
        Hasher.Filter filter = new Hasher.Filter(10);

        for (int i = 0; i < 10; i++) {
            assertTrue(filter.test(i));
        }

        for (int i = 0; i < 10; i++) {
            assertFalse(filter.test(i));
        }

        assertThrows(IndexOutOfBoundsException.class, () -> filter.test(10));
    }

}
