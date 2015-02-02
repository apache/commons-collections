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

/**
 * Provides utility methods and decorators for {@link Iterable} instances.
 *
 * @since 4.1
 * @version $Id$
 */
public class IterableUtils {

    /**
     * Default prefix used while converting an Iterable to its String representation.
     */
    private static final String DEFAULT_TOSTRING_PREFIX = "[";

    /**
     * Default suffix used while converting an Iterable to its String representation.
     */
    private static final String DEFAULT_TOSTRING_SUFFIX = "]";

    /**
     * Default delimiter used to delimit elements while converting an Iterable
     * to its String representation.
     */
    private static final String DEFAULT_TOSTRING_DELIMITER = ", ";

    /**
     * Returns a string representation of the elements of the specified iterable.
     * The string representation consists of a list of the iterable's elements,
     * enclosed in square brackets ({@code "[]"}). Adjacent elements are separated
     * by the characters {@code ", "} (a comma followed by a space). Elements are
     * converted to strings as by {@code String.valueOf(Object)}.
     *
     * @param <C>  the element type
     * @param iterable  the iterable to convert to a string
     * @return a string representation of {@code iterable}
     * @throws IllegalArgumentException if {@code iterable} is null
     */
    public static <C> String toString(Iterable<C> iterable) {
        return toString(iterable, new Transformer<C, String>() {
            public String transform(C input) {
                return String.valueOf(input);
            }
        }, DEFAULT_TOSTRING_DELIMITER, DEFAULT_TOSTRING_PREFIX, DEFAULT_TOSTRING_SUFFIX);
    }

    /**
     * Returns a string representation of the elements of the specified iterable.
     * The string representation consists of a list of the iterable's elements,
     * enclosed in square brackets ({@code "[]"}). Adjacent elements are separated
     * by the characters {@code ", "} (a comma followed by a space). Elements are
     * converted to strings as by using the provided {@code transformer}.
     *
     * @param <C>  the element type
     * @param iterable  the iterable to convert to a string
     * @param transformer  the transformer used to get a string representation of an element
     * @return a string representation of {@code iterable}
     * @throws IllegalArgumentException if {@code iterable} or {@code transformer} is null
     */
    public static <C> String toString(Iterable<C> iterable, Transformer<? super C, String> transformer) {
        return toString(iterable, transformer, DEFAULT_TOSTRING_DELIMITER,
                        DEFAULT_TOSTRING_PREFIX, DEFAULT_TOSTRING_SUFFIX);
    }

    /**
     * Returns a string representation of the elements of the specified iterable.
     * The string representation consists of a list of the iterable's elements,
     * enclosed by the provided {@code prefix} and {@code suffix}. Adjacent elements
     * are separated by the provided {@code delimiter}. Elements are converted to
     * strings as by using the provided {@code transformer}.
     *
     * @param <C>  the element type
     * @param iterable  the iterable to convert to a string
     * @param transformer  the transformer used to get a string representation of an element
     * @param delimiter  the string to delimit elements
     * @param prefix  the prefix, prepended to the string representation
     * @param suffix  the suffix, appended to the string representation
     * @return a string representation of {@code iterable}
     * @throws IllegalArgumentException if any argument is null
     */
    public static <C> String toString(Iterable<C> iterable,
                                      Transformer<? super C, String> transformer,
                                      String delimiter,
                                      String prefix,
                                      String suffix) {
        if (iterable == null) {
            throw new IllegalArgumentException("iterable may not be null");
        }
        if (transformer == null) {
            throw new IllegalArgumentException("transformer may not be null");
        }
        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter may not be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("prefix may not be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("suffix may not be null");
        }
        final StringBuilder stringBuilder = new StringBuilder(prefix);
        for(final C element : iterable) {
            stringBuilder.append(transformer.transform(element));
            stringBuilder.append(delimiter);
        }
        if(stringBuilder.length() > prefix.length()) {
            stringBuilder.setLength(stringBuilder.length() - delimiter.length());
        }
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }
}
