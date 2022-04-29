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
import java.util.stream.Stream;

import org.apache.commons.collections4.BulkTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public abstract class AbstractPropertiesFactoryTest<T extends Properties> {

    public static Stream<Arguments> getParameters() {
        return Stream.of(
                arguments(".properties"),
                arguments(".xml")
        );
    }

    private final AbstractPropertiesFactory<T> factory;

    protected AbstractPropertiesFactoryTest(final AbstractPropertiesFactory<T> factory) {
        this.factory = factory;
    }

    private void assertContents(final T properties) {
        Assertions.assertEquals("value1", properties.getProperty("key1"));
        Assertions.assertEquals("value2", properties.getProperty("key2"));
        Assertions.assertEquals("value3", properties.getProperty("key3"));
        Assertions.assertEquals("value4", properties.getProperty("key4"));
        Assertions.assertEquals("value5", properties.getProperty("key5"));
        Assertions.assertEquals("value6", properties.getProperty("key6"));
        Assertions.assertEquals("value7", properties.getProperty("key7"));
        Assertions.assertEquals("value8", properties.getProperty("key8"));
        Assertions.assertEquals("value9", properties.getProperty("key9"));
        Assertions.assertEquals("value10", properties.getProperty("key10"));
        Assertions.assertEquals("value11", properties.getProperty("key11"));
    }

    private boolean isXmlTest(final String fileExtension) {
        return ".xml".equals(fileExtension);
    }

    private String getPathString(final String fileExtension) {
        return BulkTest.TEST_PROPERTIES_PATH + "test" + fileExtension;
    }

    @Test
    public void testInstance() {
        Assertions.assertNotNull(PropertiesFactory.INSTANCE);
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadClassLoaderMissingResource(final String fileExtension) throws Exception {
        Assertions.assertNull(factory.load(ClassLoader.getSystemClassLoader(), "missing/test" + fileExtension));
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadClassLoaderResource(final String fileExtension) throws Exception {
        assertContents(factory.load(ClassLoader.getSystemClassLoader(), "org/apache/commons/collections4/properties/test" + fileExtension));
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadFile(final String fileExtension) throws Exception {
        assertContents(factory.load(Paths.get(getPathString(fileExtension)).toFile()));
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadFileName(final String fileExtension) throws Exception {
        assertContents(factory.load(getPathString(fileExtension)));
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadInputStream(final String fileExtension) throws Exception {
        // Can't tell what we are reading
        Assumptions.assumeFalse(isXmlTest(fileExtension));
        //
        try (FileInputStream inputStream = new FileInputStream(getPathString(fileExtension))) {
            assertContents(factory.load(inputStream));
        }
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadPath(final String fileExtension) throws Exception {
        assertContents(factory.load(Paths.get(getPathString(fileExtension))));
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadReader(final String fileExtension) throws Exception {
        // Can't tell what we are reading
        Assumptions.assumeFalse(isXmlTest(fileExtension));
        //
        try (BufferedReader inputStream = Files.newBufferedReader(Paths.get(getPathString(fileExtension)))) {
            assertContents(factory.load(inputStream));
        }
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadUri(final String fileExtension) throws Exception {
        assertContents(factory.load(Paths.get(getPathString(fileExtension)).toUri()));
    }

    @ParameterizedTest
    @MethodSource(value = "getParameters")
    public void testLoadUrl(final String fileExtension) throws Exception {
        assertContents(factory.load(Paths.get(getPathString(fileExtension)).toUri().toURL()));
    }

}
