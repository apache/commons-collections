/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.functors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.junit.jupiter.api.Test;

/**
 * Tests that SwitchTransformer.switchTransformer(Map) does not mutate the input map.
 */
class SwitchTransformerMapMutatesTest {

    /**
     * Tests that switchTransformer(Map) does NOT mutate the input map by removing the null key.
     */
    @Test
    void testSwitchTransformerMapDoesNotMutateInput() {
        final Transformer<String, String> defaultTransformer = ConstantTransformer.constantTransformer("default");
        final Transformer<String, String> transformer = ConstantTransformer.constantTransformer("value");
        final Predicate<String> predicate = NullPredicate.INSTANCE;
        @SuppressWarnings("unchecked")
        final Map<Predicate<String>, Transformer<String, String>> map = new LinkedHashMap<>();
        map.put(null, defaultTransformer);
        map.put(predicate, transformer);
        final int sizeBefore = map.size();
        assertEquals(2, sizeBefore);
        // Call the factory method
        SwitchTransformer.switchTransformer(map);
        // The map should NOT have been mutated - null key must still be present
        final int sizeAfter = map.size();
        assertEquals(sizeBefore, sizeAfter, "switchTransformer must not mutate the input map; expected size");
    }
}
