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

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Test;


/**
 *  Tests for BufferUtils.
 */
public class TestBufferUtils extends BulkTest {

    public TestBufferUtils(String name) {
        super(name);
    }


    public static Test suite() {
        return BulkTest.makeSuite(TestBufferUtils.class);
    }

    public void testNothing() {
    }

    public BulkTest bulkTestPredicatedBuffer() {
        return new TestPredicatedCollection("") {

            public Collection predicatedCollection() {
                Predicate p = getPredicate();
                return BufferUtils.predicatedBuffer(new ArrayStack(), p);
            }

            public BulkTest bulkTestAll() {
                return new TestCollection("") {
                    public Collection makeCollection() {
                        return predicatedCollection();
                    }

                    public Collection makeConfirmedCollection() {
                        return new ArrayStack();
                    }

                    public Collection makeConfirmedFullCollection() {
                        ArrayStack list = new ArrayStack();
                        list.addAll(java.util.Arrays.asList(getFullElements()));
                        return list;
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


    public BulkTest bulkTestUnmodifiableBuffer() {
        return new TestCollection("") {
            public boolean isAddSupported() {
                return false;
            }

            public boolean isRemoveSupported() {
                return false;
            }

            public Collection makeCollection() {
                return BufferUtils.unmodifiableBuffer(new ArrayStack());
            }

            public Collection makeFullCollection() {
                ArrayStack a = new ArrayStack();
                a.addAll(Arrays.asList(getFullElements()));
                return BufferUtils.unmodifiableBuffer(a);
            }


            public Collection makeConfirmedCollection() {
                return new ArrayStack();
            }

            public Collection makeConfirmedFullCollection() {
                ArrayStack a = new ArrayStack();
                a.addAll(Arrays.asList(getFullElements()));
                return a;
            }

        };
    }


}


