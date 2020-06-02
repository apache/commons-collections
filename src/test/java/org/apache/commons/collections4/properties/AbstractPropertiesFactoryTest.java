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

package org.apache.commons.collections4.properties;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.collections4.BulkTest;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractPropertiesFactoryTest<T extends Properties> {

    @Parameters(name = "{0}")
    public static Object[][] getParameters() {
        return new Object[][] { { ".properties" }, { ".xml" } };

    }

    private final AbstractPropertiesFactory<T> factory;
    private final String pathString;
    private final String fileExtension;

    protected AbstractPropertiesFactoryTest(final AbstractPropertiesFactory<T> factory, final String fileExtension) {
        super();
        this.factory = factory;
        this.fileExtension = fileExtension;
        this.pathString = BulkTest.TEST_PROPERTIES_PATH + "test" + fileExtension;
    }

    private void assertContents(final T properties) {
        Assert.assertEquals("value1", properties.getProperty("key1"));
        Assert.assertEquals("value2", properties.getProperty("key2"));
        Assert.assertEquals("value3", properties.getProperty("key3"));
        Assert.assertEquals("value4", properties.getProperty("key4"));
        Assert.assertEquals("value5", properties.getProperty("key5"));
        Assert.assertEquals("value6", properties.getProperty("key6"));
        Assert.assertEquals("value7", properties.getProperty("key7"));
        Assert.assertEquals("value8", properties.getProperty("key8"));
        Assert.assertEquals("value9", properties.getProperty("key9"));
        Assert.assertEquals("value10", properties.getProperty("key10"));
        Assert.assertEquals("value11", properties.getProperty("key11"));
    }

    private boolean isXmlTest() {
        return ".xml".equals(fileExtension);
    }

    @Test
    public void testInstance() {
        Assert.assertNotNull(PropertiesFactory.INSTANCE);
    }

    @Test
    public void testLoadClassLoaderMissingResource() throws Exception {
        Assert.assertNull(factory.load(ClassLoader.getSystemClassLoader(), "missing/test" + fileExtension));
    }

    @Test
    public void testLoadClassLoaderResource() throws Exception {
        assertContents(factory.load(ClassLoader.getSystemClassLoader(), "org/apache/commons/collections4/properties/test" + fileExtension));
    }

    @Test
    public void testLoadFile() throws Exception {
        assertContents(factory.load(Paths.get(pathString).toFile()));
    }

    @Test
    public void testLoadFileName() throws Exception {
        assertContents(factory.load(pathString));
    }

    @Test
    public void testLoadInputStream() throws Exception {
        // Can't tell what we are reading
        Assume.assumeFalse(isXmlTest());
        //
        try (FileInputStream inputStream = new FileInputStream(pathString)) {
            assertContents(factory.load(inputStream));
        }
    }

    @Test
    public void testLoadPath() throws Exception {
        assertContents(factory.load(Paths.get(pathString)));
    }

    @Test
    public void testLoadReader() throws Exception {
        // Can't tell what we are reading
        Assume.assumeFalse(isXmlTest());
        //
        try (BufferedReader inputStream = Files.newBufferedReader(Paths.get(pathString))) {
            assertContents(factory.load(inputStream));
        }
    }

    @Test
    public void testLoadUri() throws Exception {
        assertContents(factory.load(Paths.get(pathString).toUri()));
    }

    @Test
    public void testLoadUrl() throws Exception {
        assertContents(factory.load(Paths.get(pathString).toUri().toURL()));
    }
}
