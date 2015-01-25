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
     * Default prefix used while converting Iterable to its String representation
     */
    private static final String DEFAULT_TOSTRING_PREFIX = "[";
    
    /**
     * Default suffix used while converting Iterable to its String representation
     */
    private static final String DEFAULT_TOSTRING_SUFFIX = "]";
    
    /**
     * Default delimiter used to delimit each Iterable element 
     * while converting Iterable to its String representation
     */
    private static final String DEFAULT_TOSTRING_DELIMITER = ",";
    
    /**
     * Converts the specified Iterable into a String representation.
     * <p>
     * Elements will be delimited by a comma. The resulting representation will be surrounded by square brackets.
     * @param <C> iterable type
     * @param iterable the iterable to read
     * @return the iterable String representation
     * @throws IllegalArgumentException if iterable is null
     * @since 4.1
     */
    public static <C> String toString(Iterable<C> iterable){
        return toString(iterable, new Transformer<C, String>() {
            public String transform(C input) {
                return input.toString();
            }
        }, DEFAULT_TOSTRING_DELIMITER, DEFAULT_TOSTRING_PREFIX, DEFAULT_TOSTRING_SUFFIX);
    }
    
    /**
     * Converts the specified Iterable into a String representation using the specified
     * Transformer in order to convert each Iterable element into its own String representation.
     * <p>
     * Elements will be delimited by a comma. The resulting representation will be surrounded by square brackets.
     * @param <C> iterable type
     * @param iterable the iterable to read
     * @param transformer the transformer used to convert each element into its own String representation
     * @return the iterable String representation
     * @throws IllegalArgumentException if iterable or transformer is null
     * @since 4.1
     */
    public static <C> String toString(Iterable<C> iterable, Transformer<C, String> transformer){
        return toString(iterable, transformer, DEFAULT_TOSTRING_DELIMITER, DEFAULT_TOSTRING_PREFIX, DEFAULT_TOSTRING_SUFFIX);
    }
    
    /**
     * Converts the specified Iterable into a String representation using the specified
     * Transformer in order to convert each Iterable element into its own String representation.
     * <p>
     * Elements will be delimited by the specified delimiter. The resulting representation will be 
     * surrounded by the provided prefix and suffix.
     * @param <C> iterable type
     * @param iterable the iterable to read
     * @param transformer the transformer used to convert each element into its own String representation
     * @param delimiter the char sequence used to delimit each iterable element
     * @param prefix the iterable string representation prefix
     * @param suffix the iterable string representation suffix
     * @return the iterable String representation
     * @throws IllegalArgumentException if iterable, transformer, delimiter, prefix or suffix is null
     * @since 4.1
     */
    public static <C> String toString(Iterable<C> iterable, Transformer<C, String> transformer, String delimiter, String prefix, String suffix){
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
        StringBuilder stringBuilder = new StringBuilder(prefix);
        for(C element : iterable){
            stringBuilder.append(transformer.transform(element));
            stringBuilder.append(delimiter);
        }
        if(stringBuilder.length() > prefix.length()){
            stringBuilder.setLength(stringBuilder.length() - delimiter.length());
        }
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }
}
