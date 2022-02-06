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
package org.apache.commons.collections4.bloomfilter.hasher.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.jupiter.api.Test;

/**
 * Tests the Filter class.
 */
public class BitMapTrackerTest {

    @Test
    public void testSeen() {
        Shape shape = Shape.fromKM(3, 12);
        IndexTracker tracker = new BitMapTracker(shape);

        assertFalse( tracker.seen(0) );
        assertTrue( tracker.seen(0) );
        assertFalse( tracker.seen(1) );
        assertTrue( tracker.seen(1) );
        assertFalse( tracker.seen(2) );
        assertTrue( tracker.seen(2) );

        assertFalse( tracker.seen(4) );
        assertTrue( tracker.seen(4) );

    }
}
