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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Creates and loads {@link Properties}.
 *
 * @see Properties
 * @since 4.4
 */
public class PropertiesFactory extends AbstractPropertiesFactory<Properties> {

    private static class EmptyProperties extends Properties {

        private static final long serialVersionUID = 1L;

        @Override
        public synchronized void clear() {
            // Noop
        }

        @Override
        public synchronized Object compute(final Object key,
            final BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
            Objects.requireNonNull(key);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized Object computeIfAbsent(final Object key,
            final Function<? super Object, ? extends Object> mappingFunction) {
            Objects.requireNonNull(key);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized Object computeIfPresent(final Object key,
            final BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
            Objects.requireNonNull(key);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized boolean contains(final Object value) {
            return false;
        }

        @Override
        public synchronized boolean containsKey(final Object key) {
            return false;
        }

        @Override
        public boolean containsValue(final Object value) {
            return false;
        }

        @Override
        public synchronized Enumeration<Object> elements() {
            return Collections.emptyEnumeration();
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return Collections.emptySet();
        }

        @Override
        public synchronized boolean equals(final Object o) {
            return (o instanceof Properties) && ((Properties) o).isEmpty();
        }

        @Override
        public synchronized void forEach(final BiConsumer<? super Object, ? super Object> action) {
            Objects.requireNonNull(action);
        }

        @Override
        public synchronized Object get(final Object key) {
            return null;
        }

        @Override
        public synchronized Object getOrDefault(final Object key, final Object defaultValue) {
            return defaultValue;
        }

        @Override
        public String getProperty(final String key) {
            return null;
        }

        @Override
        public String getProperty(final String key, final String defaultValue) {
            return defaultValue;
        }

        @Override
        public synchronized int hashCode() {
            return 0;
        }

        @Override
        public synchronized boolean isEmpty() {
            return true;
        }

        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.emptyEnumeration();
        }

        @Override
        public Set<Object> keySet() {
            return Collections.emptySet();
        }

        @Override
        public void list(final PrintStream out) {
            // Implement as super
            super.list(out);
        }

        @Override
        public void list(final PrintWriter out) {
            // Implement as super
            super.list(out);
        }

        @Override
        public synchronized void load(final InputStream inStream) throws IOException {
            Objects.requireNonNull(inStream);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void load(final Reader reader) throws IOException {
            Objects.requireNonNull(reader);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void loadFromXML(final InputStream in)
            throws IOException, InvalidPropertiesFormatException {
            Objects.requireNonNull(in);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized Object merge(final Object key, final Object value,
            final BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            throw new UnsupportedOperationException();
        }

        @Override
        public Enumeration<?> propertyNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public synchronized Object put(final Object key, final Object value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void putAll(final Map<? extends Object, ? extends Object> t) {
            Objects.requireNonNull(t);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized Object putIfAbsent(final Object key, final Object value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            throw new UnsupportedOperationException();
        }

        @Override
        protected void rehash() {
            // Noop
        }

        @Override
        public synchronized Object remove(final Object key) {
            Objects.requireNonNull(key);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized boolean remove(final Object key, final Object value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized Object replace(final Object key, final Object value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized boolean replace(final Object key, final Object oldValue, final Object newValue) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(oldValue);
            Objects.requireNonNull(newValue);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void replaceAll(
            final BiFunction<? super Object, ? super Object, ? extends Object> function) {
            Objects.requireNonNull(function);
            throw new UnsupportedOperationException();
        }

        @Override
        public void save(final OutputStream out, final String comments) {
            // Implement as super
            super.save(out, comments);
        }

        @Override
        public synchronized Object setProperty(final String key, final String value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized int size() {
            return 0;
        }

        @Override
        public void store(final OutputStream out, final String comments) throws IOException {
            // Implement as super
            super.store(out, comments);
        }

        @Override
        public void store(final Writer writer, final String comments) throws IOException {
            // Implement as super
            super.store(writer, comments);
        }

        @Override
        public void storeToXML(final OutputStream os, final String comment) throws IOException {
            // Implement as super
            super.storeToXML(os, comment);
        }

        @Override
        public void storeToXML(final OutputStream os, final String comment, final String encoding) throws IOException {
            // Implement as super
            super.storeToXML(os, comment, encoding);
        }

        @Override
        public Set<String> stringPropertyNames() {
            return Collections.emptySet();
        }

        @Override
        public synchronized String toString() {
            // Implement as super
            return super.toString();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptyList();
        }

    }

    /**
     * The empty map (immutable). This map is serializable.
     *
     * @since 4.5
     */
    public static final Properties EMPTY_PROPERTIES = new EmptyProperties();

    /**
     * The singleton instance.
     */
    public static final PropertiesFactory INSTANCE = new PropertiesFactory();

    /**
     * Constructs an instance.
     */
    private PropertiesFactory() {
        // There is only one instance.
    }

    /**
     * Subclasses override to provide customized properties instances.
     *
     * @return a new Properties instance.
     */
    @Override
    protected Properties createProperties() {
        return new Properties();
    }

}
