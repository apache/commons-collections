package org.apache.commons.collections;

import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class TestUtils {

    private TestUtils() {
    }

    /**
     * Asserts that deserialization of the object returns the same object as the
     * one that was serialized. Object is first serialized, then deserialized
     * and finally check is preformed to see if original and deserialized
     * object references are the same.
     * <p>
     * This method is especially good for testing singletone pattern on classes
     * that support serialization.
     *
     * @param msg the identifying message for the <code>AssertionError</code>.
     * @param o object that will be tested.
     * @see #assertSameAfterSerialization(Object)
     */
    public static void assertSameAfterSerialization(String msg, Object o) {
        try {
            // write object to byte buffer
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();

            // read same object from byte buffer
            final InputStream is = new ByteArrayInputStream(baos.toByteArray());
            final ObjectInputStream ois = new ObjectInputStream(is);
            final Object object = ois.readObject();
            ois.close();

            // assert that original object and deserialized objects are the same
            assertSame(msg, o, object);
        } catch (IOException e) {
            // should never happen
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Asserts that deserialization of the object returns the same object as the
     * one that was serialized.
     * <p>
     * Effect of method call is the same as:
     * <code>assertSameAfterSerialization(null, o)</code>.
     *
     * @param o object that will be tested.
     * @see #assertSameAfterSerialization(String, Object)
     */
    public static void assertSameAfterSerialization(Object o) {
        assertSameAfterSerialization(null, o);
    }
}
