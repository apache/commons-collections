/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.functors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestPrototypeFactory extends AbstractTestSerialization {

    // conventional
    // ------------------------------------------------------------------------

    public TestPrototypeFactory(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPrototypeFactory.class);
    }

    // ------------------------------------------------------------------------

    public Object makeObject() {
        return PrototypeFactory.getInstance(new ArrayList());
    }

    public Class getTestClass() {
        return Factory.class;
    }

    // ------------------------------------------------------------------------

    public void testPrototypeFactoryPublicCloneMethod() throws Exception {
        Date proto = new Date();
        Factory factory = PrototypeFactory.getInstance(proto);
        assertNotNull(factory);
        Object created = factory.create();
        assertTrue(proto != created);
        assertEquals(proto, created);
        
        // check serialisation works - if enabled
        System.setProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY, "true");
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(factory);
            out.close();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            Object dest = in.readObject();
            in.close();
        } finally {
            System.clearProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY);
        }
    }

    public void testPrototypeFactoryPublicCopyConstructor() throws Exception {
        Mock1 proto = new Mock1(6);
        Factory factory = PrototypeFactory.getInstance(proto);
        assertNotNull(factory);
        Object created = factory.create();
        assertTrue(proto != created);
        assertEquals(proto, created);
        
        // check serialisation works - if enabled
        System.setProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY, "true");
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            try {
                out.writeObject(factory);
            } catch (NotSerializableException ex) {
                out.close();
            }
            factory = FactoryUtils.prototypeFactory(new Mock2("S"));
            buffer = new ByteArrayOutputStream();
            out = new ObjectOutputStream(buffer);
            out.writeObject(factory);
            out.close();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            Object dest = in.readObject();
            in.close();
        } finally {
            System.clearProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY);
        }
    }

    public void testPrototypeFactoryPublicSerialization() throws Exception {
        Integer proto = new Integer(9);
        Factory factory = FactoryUtils.prototypeFactory(proto);
        assertNotNull(factory);
        Object created = factory.create();
        assertTrue(proto != created);
        assertEquals(proto, created);
        
        // check serialisation works - if enabled
        System.setProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY, "true");
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(factory);
            out.close();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            Object dest = in.readObject();
            in.close();
        } finally {
            System.clearProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY);
        }
    }

    // ------------------------------------------------------------------------

    private static class Mock1 {
        private final int iVal;
        public Mock1(int val) {
            iVal = val;
        }
        public Mock1(Mock1 mock) {
            iVal = mock.iVal;
        }
        public boolean equals(Object obj) {
            if (obj instanceof Mock1) {
                if (iVal == ((Mock1) obj).iVal) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class Mock2 implements Serializable {
        private final Object iVal;
        public Mock2(Object val) {
            iVal = val;
        }
        public boolean equals(Object obj) {
            if (obj instanceof Mock2) {
                if (iVal == ((Mock2) obj).iVal) {
                    return true;
                }
            }
            return false;
        }
    }

}
