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
public class BulkTest implements Cloneable {

    // Note:  BulkTest is Cloneable to make it easier to construct
    // BulkTest instances for simple test methods that are defined in
    // anonymous inner classes.  Basically we don't have to worry about
    // finding weird constructors.  (And even if we found them, technically
    // it'd be illegal for anyone but the outer class to invoke them).
    // Given one BulkTest instance, we can just clone it and reset the
    // method name for every simple test it defines.

    /** Path to test data resources */
    protected static final String TEST_DATA_PATH = "src/test/resources/org/apache/commons/collections4/data/test/";

    /** Path to test properties resources */
    public static final String TEST_PROPERTIES_PATH = "src/test/resources/org/apache/commons/collections4/properties/";

    /**
     *  The full name of this bulk test instance.  This is the full name
     *  that is compared to {@link #ignoredTests} to see if this
     *  test should be ignored.  It's also displayed in the text runner
     *  to ease debugging.
     */
    private String verboseName;

    /**
     *  the name of the simple test method
     */
    private String name;

    /**
     *  Constructs a new {@code BulkTest} instance that will run the
     *  specified simple test.
     */
    public BulkTest() {
        this.name = getClass().getSimpleName();
        this.verboseName = getClass().getName();
    }

    /**
     * Creates a clone of this {@code BulkTest}.
     *
     * @return a clone of this {@code BulkTest}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     *  Returns the name of the simple test method of this {@code BulkTest}.
     *
     *  @return the name of the simple test method of this {@code BulkTest}
     */
    public String getName() {
        return name;
    }

    /**
     *  Returns an array of test names to ignore.<P>
     *
     *  If a test that's defined by this {@code BulkTest} or
     *  by one of its bulk test methods has a name that's in the returned
     *  array, then that simple test will not be executed.<P>
     *
     *  A test's name is formed by taking the class name of the
     *  root {@code BulkTest}, eliminating the package name, then
     *  appending the names of any bulk test methods that were invoked
     *  to get to the simple test, and then appending the simple test
     *  method name.  The method names are delimited by periods:
     *
     *  <pre>
     *  HashMapTest.bulkTestEntrySet.testClear
     *  </pre>
     *
     *  is the name of one of the simple tests defined in the sample classes
     *  described above.  If the sample {@code HashMapTest} class
     *  included this method:
     *
     *  <pre>
     *  public String[] ignoredTests() {
     *      return new String[] { "HashMapTest.bulkTestEntrySet.testClear" };
     *  }
     *  </pre>
     *
     *  then the entry set's clear method wouldn't be tested, but the key
     *  set's clear method would.
     *
     *  @return an array of the names of tests to ignore, or null if
     *   no tests should be ignored
     */
    public String[] ignoredTests() {
        return null;
    }

    /**
     *  Returns the display name of this {@code BulkTest}.
     *
     *  @return the display name of this {@code BulkTest}
     */
    @Override
    public String toString() {
        return getName() + "(" + verboseName + ") ";
    }

}
