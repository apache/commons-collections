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

import org.apache.commons.collections.BulkTest;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestInvokerTransformer extends BulkTest {

    // conventional
    // ------------------------------------------------------------------------

    public TestInvokerTransformer(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestInvokerTransformer.class);
    }

    // ------------------------------------------------------------------------

    public void testSerializationDisabled() throws Exception {
        Assert.assertNull(System.getProperty(InvokerTransformer.DESERIALIZE));
        InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        byte[] data = serialize(transformer);
        Assert.assertNotNull(data);
        try {
            deserialize(data);
            fail("de-serialization of InvokerTransformer should be disabled by default");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    public void testSerializationEnabled() throws Exception {
        Assert.assertNull(System.getProperty(InvokerTransformer.DESERIALIZE));
        System.setProperty(InvokerTransformer.DESERIALIZE, "true");

        try {
            InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
            byte[] data = serialize(transformer);
            Assert.assertNotNull(data);
            try {
                Object obj = deserialize(data);
                Assert.assertTrue(obj instanceof InvokerTransformer);
            } catch (UnsupportedOperationException ex) {
                fail("de-serialization of InvokerTransformer should be enabled");
            }
        } finally {
            System.clearProperty(InvokerTransformer.DESERIALIZE);
        }
    }
    
    private byte[] serialize(InvokerTransformer transformer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(transformer);
        oos.close();

        return baos.toByteArray();
    }
    
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream iis = new ObjectInputStream(bais);
        
        return iis.readObject();
    }

}
