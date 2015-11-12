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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.apache.commons.collections.BulkTest;

/**
 * Abstract test class for testing serialization support
 * of the functor package.
 */
public abstract class AbstractTestSerialization extends BulkTest {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractTestSerialization(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the abstract superclass method to return the comparator.
     * 
     * @return a full iterator
     */
    public abstract Object makeObject();

    /**
     * Returns the class being tested for serialization.
     * 
     * @return the test class
     */
    public abstract Class getTestClass();

    //-----------------------------------------------------------------------
    
    public void testSerializationDisabled() throws Exception {
        Assert.assertFalse("true".equalsIgnoreCase(System.getProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY)));
        Object object = makeObject();
        try {
            serialize(object);
            fail("serialization of InvokerTransformer should be disabled by default");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        System.setProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY, "true");
        byte[] data = serialize(object);
        System.getProperties().remove(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY);
        Assert.assertNull(System.getProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY));
        Assert.assertNotNull(data);
        try {
            deserialize(data);
            fail("de-serialization of " + getTestClass().getName() + " should be disabled by default");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    public void testSerializationEnabled() throws Exception {
        Assert.assertFalse("true".equalsIgnoreCase(System.getProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY)));
        System.setProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY, "true");

        try {
            Object object = makeObject();
            byte[] data = serialize(object);
            Assert.assertNotNull(data);
            try {
                Object obj = deserialize(data);
                Assert.assertTrue(getTestClass().isInstance(obj));
            } catch (UnsupportedOperationException ex) {
                fail("de-serialization of " + getTestClass().getName() + " should be enabled");
            }
        } finally {
            System.setProperty(FunctorUtils.UNSAFE_SERIALIZABLE_PROPERTY, "false");
        }
    }
    
    private byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(object);
        oos.close();

        return baos.toByteArray();
    }
    
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream iis = new ObjectInputStream(bais);
        
        return iis.readObject();
    }

}
