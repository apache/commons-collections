/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Test;

import org.apache.commons.collections.buffer.PredicatedBuffer;
import org.apache.commons.collections.collection.AbstractTestCollection;

/**
 * Tests for BufferUtils.
 * 
 * @version $Revision: 1.12 $ $Date: 2004/02/18 01:20:35 $
 * 
 * @author Unknown
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

    public void testpredicatedBuffer() {
        Predicate predicate = new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
        Buffer buffer = BufferUtils.predicatedBuffer(new ArrayStack(), predicate);
        assertTrue("returned object should be a PredicatedBuffer",
            buffer instanceof PredicatedBuffer);
        try {
            buffer = BufferUtils.predicatedBuffer(new ArrayStack(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            buffer = BufferUtils.predicatedBuffer(null, predicate);
            fail("Expecting IllegalArgumentException for null buffer.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }


    public BulkTest bulkTestUnmodifiableBuffer() {
        return new AbstractTestCollection("") {
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
