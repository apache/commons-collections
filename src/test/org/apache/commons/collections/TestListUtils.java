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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;


public class TestListUtils extends BulkTest {

    public TestListUtils(String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestListUtils.class);
    }

    public void testNothing() {
    }

    public BulkTest bulkTestPredicatedList() {
        return new TestPredicatedCollection("") {

            public Collection predicatedCollection() {
                Predicate p = getPredicate();
                return ListUtils.predicatedList(new ArrayList(), p);
            }

            public BulkTest bulkTestAll() {
                return new TestList("") {
                    public List makeEmptyList() {
                        return (List)predicatedCollection();
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


    public void testLazyList() {
        List list = ListUtils.lazyList(new ArrayList(), new Factory() {

            private int index;

            public Object create() {
                index++;
                return new Integer(index);
            }
        });

        Integer I = (Integer)list.get(5);
        assertEquals(6, list.size());

        I = (Integer)list.get(5);
        assertEquals(6, list.size());
    }


}


