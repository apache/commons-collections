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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.IntPredicate;

import org.apache.commons.collections4.bloomfilter.Hasher.IndexFilter.BitMapTracker;
import org.junit.jupiter.api.Test;

/**
 * Tests the Filter class.
 */
public class BitMapTrackerTest {

    @Test
    public void testSeen() {
        Shape shape = Shape.fromKM(3, 12);
        IntPredicate tracker = new BitMapTracker(shape);

        assertTrue(tracker.test(0));
        assertFalse(tracker.test(0));
        assertTrue(tracker.test(1));
        assertFalse(tracker.test(1));
        assertTrue(tracker.test(2));
        assertFalse(tracker.test(2));

        assertTrue(tracker.test(4));
        assertFalse(tracker.test(4));
    }
}
