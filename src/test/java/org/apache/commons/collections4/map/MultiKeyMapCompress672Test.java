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

package org.apache.commons.collections4.map;

import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests COMPRESS-872
 * <p>
 * Claim:
 * </p>
 * <ol>
 * <li>Create a MultiKeyMap, with key(s) of a type (class/record) which has some fields.
 * <li>Use multiKeyMap.put(T... keys, V value), to create an entry in the Map, to map the keys to a value
 * <li>Use multiKeyMap.get(T... keys), to verify that the mapping exists and returns the expected value.
 * <li>Modify/alter any of the objects used as a key. It is enough to change the value of any member field of any of the objects.
 * <li>Use multiKeyMap.get(T... keys) again, however, now there is no mapping for these keys!
 * <li>Use multiKeyMap.get(T... keys) with the new modified/altered objects, and it will return the expected value
 * </ol>
 * COUNTER CLAIM: The class is documented to use MultiKey which documents key elements as "The keys should be immutable".
 */
public class MultiKeyMapCompress672Test {

    private static final class KeyFixture {

        private String value = "originalValue";

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            final KeyFixture object = (KeyFixture) obj;
            if (getValue() == null) {
                return object.getValue() == null;
            }
            return getValue().equals(object.getValue());
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        public void setValue(final String field) {
            this.value = field;
        }
    }

    private static final String KEY_1 = "key1";

    private final MultiKeyMap<Object, String> multiKeyMap = new MultiKeyMap<>();

    @Test
    public void testMutateKey() {
        // Both objects have the 'field' value set to "originalValue"
        final KeyFixture keyFixture2Ro = new KeyFixture();
        final KeyFixture keyFixture2Rw = new KeyFixture();
        // Put original mapping
        Assertions.assertNull(multiKeyMap.put(KEY_1, keyFixture2Rw, "value"));
        // Both mappings are correct
        Assertions.assertEquals("value", multiKeyMap.get(KEY_1, keyFixture2Rw));
        Assertions.assertEquals("value", multiKeyMap.get(KEY_1, keyFixture2Ro));
        // Modify 'fixture2'
        keyFixture2Rw.setValue("newValue");
        // Modified mapping SHOULD NOT work
        Assertions.assertNull(multiKeyMap.get(KEY_1, keyFixture2Rw));
        // Claim: Original mapping SHOULD work
        // COUNTER CLAIM: The class is documented to use MultiKey which documents key elements as "The keys should be immutable".
        Assertions.assertNotEquals("value", multiKeyMap.get(KEY_1, keyFixture2Ro));
    }
}
