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
package org.apache.commons.collections4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import static org.apache.commons.collections4.BulkTestConstants.TEST_DATA_PATH;

/**
 * Abstract object test utils, extracted from AbstractObjectTest for easier reuse.
 */
public final class AbstractObjectTestUtils {

    public static String getCanonicalEmptyCollectionName(final Object object, final AbstractObjectTestInterface test) {
        final StringBuilder retval = new StringBuilder();
        retval.append(TEST_DATA_PATH);
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".") + 1);
        retval.append(colName);
        retval.append(".emptyCollection.version");
        retval.append(test.getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    public static String getCanonicalFullCollectionName(final Object object, final AbstractObjectTestInterface test) {
        final StringBuilder retval = new StringBuilder();
        retval.append(TEST_DATA_PATH);
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".") + 1);
        retval.append(colName);
        retval.append(".fullCollection.version");
        retval.append(test.getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    public static Object serializeDeserialize(final Object obj) throws Exception {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(buffer);
        out.writeObject(obj);
        out.close();

        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        final Object dest = in.readObject();
        in.close();

        return dest;
    }

    /**
     * Writes a Serializable or Externalizable object as
     * a file at the given path.  NOT USEFUL as part
     * of a unit test; this is just a utility method
     * for creating disk-based objects in SCM that can become
     * the basis for compatibility tests using
     * readExternalFormFromDisk(String path)
     *
     * @param o Object to serialize
     * @param path path to write the serialized Object
     * @throws IOException
     */
    public static void writeExternalFormToDisk(final Serializable o, final String path) throws IOException {
        try (FileOutputStream fileStream = new FileOutputStream(path)) {
            writeExternalFormToStream(o, fileStream);
        }
    }

    /**
     * Converts a Serializable or Externalizable object to
     * bytes.  Useful for in-memory tests of serialization
     *
     * @param o Object to convert to bytes
     * @return serialized form of the Object
     * @throws IOException
     */
    public static byte[] writeExternalFormToBytes(final Serializable o) throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        writeExternalFormToStream(o, byteStream);
        return byteStream.toByteArray();
    }

    /**
     * Reads a Serialized or Externalized Object from disk.
     * Useful for creating compatibility tests between
     * different SCM versions of the same class
     *
     * @param path path to the serialized Object
     * @return the Object at the given path
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readExternalFormFromDisk(final String path) throws IOException, ClassNotFoundException {
        try (FileInputStream stream = new FileInputStream(path)) {
            return readExternalFormFromStream(stream);
        }
    }

    /**
     * Read a Serialized or Externalized Object from bytes.
     * Useful for verifying serialization in memory.
     *
     * @param b byte array containing a serialized Object
     * @return Object contained in the bytes
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readExternalFormFromBytes(final byte[] b) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(b);
        return readExternalFormFromStream(stream);
    }

    // private implementation
    private static Object readExternalFormFromStream(final InputStream stream) throws IOException, ClassNotFoundException {
        final ObjectInputStream oStream = new ObjectInputStream(stream);
        return oStream.readObject();
    }

    private static void writeExternalFormToStream(final Serializable o, final OutputStream stream) throws IOException {
        final ObjectOutputStream oStream = new ObjectOutputStream(stream);
        oStream.writeObject(o);
    }

}
