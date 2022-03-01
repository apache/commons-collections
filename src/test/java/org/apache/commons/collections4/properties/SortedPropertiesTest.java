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
package org.apache.commons.collections4.properties;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SortedPropertiesTest {

    @Test
    public void testEntrySet() {
        final SortedProperties sortedProperties = new SortedProperties();
        for (char ch = 'Z'; ch >= 'A'; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Iterator<Map.Entry<Object, Object>> entries = sortedProperties.entrySet().iterator();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            final Map.Entry<Object, Object> entry = entries.next();
            Assertions.assertEquals(String.valueOf(ch), entry.getKey());
            Assertions.assertEquals("Value" + ch, entry.getValue());
        }
    }

    @Test
    public void testKeys() {
        final SortedProperties sortedProperties = new SortedProperties();
        for (char ch = 'Z'; ch >= 'A'; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Enumeration<Object> keys = sortedProperties.keys();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            Assertions.assertEquals(String.valueOf(ch), keys.nextElement());
        }
    }

}
