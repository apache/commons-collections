/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections;

import java.util.Map;

import junit.framework.Test;

import org.apache.commons.collections.map.AbstractTestMap;

/**
 * Unit tests 
 * {@link org.apache.commons.collections.StaticBucketMap}.
 * 
 * @version $Revision: 1.11 $ $Date: 2004/02/18 01:20:35 $
 * 
 * @author Michael A. Smith
 */
public class TestStaticBucketMap extends AbstractTestMap {

    public TestStaticBucketMap(String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestStaticBucketMap.class);
    }

    public static void main(String[] args[]) {
        String[] testCaseName = { TestStaticBucketMap.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        return new StaticBucketMap(30);
    }

    public String[] ignoredTests() {
        String pre = "TestStaticBucketMap.bulkTestMap";
        String post = ".testCollectionIteratorFailFast";
        return new String[] {
            pre + "EntrySet" + post,
            pre + "KeySet" + post,
            pre + "Values" + post
        };
    }   
}
