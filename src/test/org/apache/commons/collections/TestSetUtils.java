/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import junit.framework.Test;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;


/**
 *  Tests for SetUtils.
 */
public class TestSetUtils extends BulkTest {

    public TestSetUtils(String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestSetUtils.class);
    }

    public void testNothing() {
    }

    public BulkTest bulkTestPredicatedSet() {
        return new TestPredicatedCollection("") {

            public Collection predicatedCollection() {
                Predicate p = getPredicate();
                return SetUtils.predicatedSet(new HashSet(), p);
            }

            public BulkTest bulkTestAll() {
                return new TestSet("") {
                    public Set makeEmptySet() {
                        return (Set)predicatedCollection();
                    }

                    public Object[] getFullElements() {
                        return getFullNonNullStringElements();
                    }

                    public Object[] getOtherElements() {
                        return getOtherNonNullStringElements();
                    }

                };
            }
        };
    }


}


