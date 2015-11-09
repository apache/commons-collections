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
