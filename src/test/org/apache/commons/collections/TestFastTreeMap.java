/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.Test;

/**
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: TestFastTreeMap.java,v 1.9.2.1 2004/05/22 12:14:05 scolebourne Exp $
 */
public class TestFastTreeMap extends TestTreeMap
{
    public TestFastTreeMap(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return BulkTest.makeSuite(TestFastTreeMap.class);
    }

    public static void main(String args[])
    {
        String[] testCaseName = { TestFastTreeMap.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        FastTreeMap ftm = new FastTreeMap();
        ftm.setFast(false);
        return (ftm);
    }

    public Map makeConfirmedEmptyMap() {
        return new TreeMap();
    }
  
    /**
     *  The comparator for the fast tree map does not support null keys.
     **/
    public boolean useNullKey() {
      return false;
    }


    /**
     * There is a bug in JDK1.2.2 TreeMap; the keySet will incorrectly
     * return false when a null value is removed
     */
    public boolean useNullValue() {
       return false;
    }

    public void setUp()
    {
        map = (TreeMap) makeEmptyMap();
    }

}
