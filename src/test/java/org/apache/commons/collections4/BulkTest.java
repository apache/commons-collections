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
 * This class is left over from the JUnit 3 implementation.
 */
public class BulkTest {

    /** Path to test data resources. */
    protected static final String TEST_DATA_PATH = "src/test/resources/org/apache/commons/collections4/data/test/";

    /** Path to test properties resources. */
    public static final String TEST_PROPERTIES_PATH = "src/test/resources/org/apache/commons/collections4/properties/";

    /**
     * The full name of this bulk test instance.
     */
    private final String verboseName;

    /**
     * The name of the simple test method.
     */
    private final String name;

    /**
     * Constructs a new {@code BulkTest} instance that will run the specified simple test.
     */
    public BulkTest() {
        this.name = getClass().getSimpleName();
        this.verboseName = getClass().getName();
    }

    /**
     * Gets the name of the simple test method of this {@code BulkTest}.
     *
     * @return the name of the simple test method of this {@code BulkTest}.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the display name of this {@code BulkTest}.
     *
     * @return the display name of this {@code BulkTest}.
     */
    @Override
    public String toString() {
        return getName() + "(" + verboseName + ") ";
    }

}
