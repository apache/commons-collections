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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

/**
 * Subclasses create and load {@link Properties} and subclasses of {@link Properties} like {@link SortedProperties}.
 *
 * @param <T> {@link Properties} or a subclass like {@link SortedProperties}.
 * @see Properties
 * @since 4.4
 */
public abstract class AbstractPropertiesFactory<T extends Properties> {

    /** 
     * Enumerat5es property formats.
     *
     * @since 4.5
     */
    public enum PropertyFormat {

        /** Properties file format. */
        PROPERTIES,

        /** XML file format. */
        XML;

        static PropertyFormat toPropertyFormat(String fileName) {
            return Objects.requireNonNull(fileName, "fileName").endsWith(".xml") ? XML : PROPERTIES;
        }
    }

    /**
     * Constructs an instance.
     */
    protected AbstractPropertiesFactory() {
        // no init.
    }

    /**
     * Subclasses override to provide customized properties instances.
     *
     * @return a new Properties instance.
     */
    protected abstract T createProperties();

    /**
     * Creates and loads properties from the given file.
     *
     * @param classLoader the class loader to use to get the named resource.
     * @param name        the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     */
    public T load(final ClassLoader classLoader, final String name) throws IOException {
        try (final InputStream inputStream = classLoader.getResourceAsStream(name)) {
            return load(inputStream, PropertyFormat.toPropertyFormat(name));
        }
    }

    /**
     * Creates and loads properties from the given file.
     *
     * @param file the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     * @throws FileNotFoundException    Thrown if the file does not exist, is a directory, or cannot be opened for
     *                                  reading.
     * @throws SecurityException        Thrown if a security manager's {@code checkRead} method denies read access to
     *                                  the file.
     */
    public T load(final File file) throws FileNotFoundException, IOException {
        try (final FileInputStream inputStream = new FileInputStream(file)) {
            return load(inputStream, PropertyFormat.toPropertyFormat(file.getName()));
        }
    }

    /**
     * Creates and loads properties from the given input stream.
     *
     * @param inputStream the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     */
    public T load(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        final T properties = createProperties();
        properties.load(inputStream);
        return properties;
    }

    /**
     * Creates and loads properties from the given input stream.
     *
     * @param inputStream the location of the properties file.
     * @param propertyFormat The format of the given file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     * @since 4.5
     */
    public T load(final InputStream inputStream, final PropertyFormat propertyFormat) throws IOException {
        if (inputStream == null) {
            return null;
        }
        final T properties = createProperties();
        if (propertyFormat == PropertyFormat.XML) {
            properties.loadFromXML(inputStream);
        } else {
            properties.load(inputStream);
        }
        return properties;
    }

    /**
     * Creates and loads properties from the given path.
     *
     * @param path the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     */
    public T load(final Path path) throws IOException {
        try (final InputStream inputStream = Files.newInputStream(path)) {
            return load(inputStream, PropertyFormat.toPropertyFormat(Objects.toString(path.getFileName(), null)));
        }
    }

    /**
     * Creates and loads properties from the given reader.
     *
     * @param reader the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     */
    public T load(final Reader reader) throws IOException {
        final T properties = createProperties();
        properties.load(reader);
        return properties;
    }

    /**
     * Creates and loads properties from the given file name.
     *
     * @param name the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     */
    public T load(final String name) throws IOException {
        try (final FileInputStream inputStream = new FileInputStream(name)) {
            return load(inputStream, PropertyFormat.toPropertyFormat(name));
        }
    }

    /**
     * Creates and loads properties from the given URI.
     *
     * @param uri the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     */
    public T load(final URI uri) throws IOException {
        return load(Paths.get(uri));
    }

    /**
     * Creates and loads properties from the given URL.
     *
     * @param url the location of the properties file.
     * @return a new properties object.
     * @throws IOException              Thrown if an error occurred reading the input stream.
     * @throws IllegalArgumentException Thrown if the input contains a malformed Unicode escape sequence.
     */
    public T load(final URL url) throws IOException {
        try (final InputStream inputStream = url.openStream()) {
            return load(inputStream, PropertyFormat.toPropertyFormat(url.getFile()));
        }
    }

}
