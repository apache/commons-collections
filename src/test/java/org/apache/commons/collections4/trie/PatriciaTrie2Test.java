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
package org.apache.commons.collections4.trie;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.AbstractOrderedMapTest;

/**
 * JUnit test of the OrderedMap interface of a PatriciaTrie.
 *
 * @since 4.0
 * @version $Id$
 */
public class PatriciaTrie2Test<V> extends AbstractOrderedMapTest<String, V> {

    public PatriciaTrie2Test(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(PatriciaTrie2Test.class);
    }

    @Override
    public OrderedMap<String, V> makeObject() {
        return new PatriciaTrie<V>();
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    //-----------------------------------------------------------------------

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

}
