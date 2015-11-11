/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.functors;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestInstantiateTransformer extends AbstractTestSerialization {

    // conventional
    // ------------------------------------------------------------------------

    public TestInstantiateTransformer(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestInstantiateTransformer.class);
    }

    // ------------------------------------------------------------------------

    public Object makeObject() {
        return new InstantiateTransformer(new Class[0], new Object[0]);
    }

    public Class getTestClass() {
        return InstantiateTransformer.class;
    }

}
