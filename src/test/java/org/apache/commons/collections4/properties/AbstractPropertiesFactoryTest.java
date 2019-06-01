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

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractPropertiesFactoryTest<T extends Properties> {

    private static final String PATH_STRING = "src/test/resources/properties/test.properties";

    private final AbstractPropertiesFactory<T> factory;

    protected AbstractPropertiesFactoryTest(final AbstractPropertiesFactory<T> factory) {
        super();
        this.factory = factory;
    }

    private void assertContents(final T properties) {
        Assert.assertEquals("value1", properties.getProperty("key1"));
        Assert.assertEquals("value2", properties.getProperty("key2"));
    }

    @Test
    public void testInstance() {
        Assert.assertNotNull(PropertiesFactory.INSTANCE);
    }

    @Test
    public void testLoadClassLoaderMissingResource() throws Exception {
        Assert.assertNull(factory.load(ClassLoader.getSystemClassLoader(), "missing/test.properties"));
    }

    @Test
    public void testLoadClassLoaderResource() throws Exception {
        assertContents(factory.load(ClassLoader.getSystemClassLoader(), "properties/test.properties"));
    }

    @Test
    public void testLoadFile() throws Exception {
        assertContents(factory.load(Paths.get(PATH_STRING).toFile()));
    }

    @Test
    public void testLoadFileName() throws Exception {
        assertContents(factory.load(PATH_STRING));
    }

    @Test
    public void testLoadInputStream() throws Exception {
        try (final FileInputStream inputStream = new FileInputStream(PATH_STRING)) {
            assertContents(factory.load(inputStream));
        }
    }

    @Test
    public void testLoadPath() throws Exception {
        assertContents(factory.load(Paths.get(PATH_STRING)));
    }

    @Test
    public void testLoadReader() throws Exception {
        try (final BufferedReader inputStream = Files.newBufferedReader(Paths.get(PATH_STRING))) {
            assertContents(factory.load(inputStream));
        }
    }

    @Test
    public void testLoadUri() throws Exception {
        assertContents(factory.load(Paths.get(PATH_STRING).toUri()));
    }

    @Test
    public void testLoadUrl() throws Exception {
        assertContents(factory.load(Paths.get(PATH_STRING).toUri().toURL()));
    }
}
