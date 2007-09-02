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
package org.apache.commons.collections;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Entry point for all Collections project tests.
 * 
 * @version $Revision$ $Date$
 * 
 * @author Stephen Colebourne
 * @author Stephen Kestle
 */
@RunWith(Suite.class)
@SuiteClasses({
	org.apache.commons.collections.TestAll.class,
    org.apache.commons.collections.bag.TestAll.class,
    org.apache.commons.collections.bidimap.TestAll.class,
    org.apache.commons.collections.buffer.TestAll.class,
    org.apache.commons.collections.collection.TestAll.class,
    org.apache.commons.collections.comparators.TestAll.class,
    org.apache.commons.collections.iterators.TestAll.class,
    org.apache.commons.collections.keyvalue.TestAll.class,
    org.apache.commons.collections.list.TestAll.class,
    org.apache.commons.collections.map.TestAll.class,
    org.apache.commons.collections.set.TestAll.class
})
public class TestAllPackages {
}
