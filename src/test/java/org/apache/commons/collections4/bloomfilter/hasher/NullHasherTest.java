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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link NullHasher}.
 */
public class NullHasherTest {

    private Hasher hasher = NullHasher.INSTANCE;

    @Test
    public void sizeTest() {
        assertEquals( 0, hasher.size() );
    }

    @Test
    public void testIterator() {
        Shape shape = new Shape( 5, 10 );
        List<Integer> lst = new ArrayList<Integer>();
        IndexProducer producer = hasher.indices(shape);
        producer.forEachIndex( lst::add );
        assertEquals( 0, lst.size());
    }


}
