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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

/**
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: TestFastArrayList.java,v 1.6.2.1 2004/05/22 12:14:05 scolebourne Exp $
 */
public class TestFastArrayList extends TestArrayList
{
    public TestFastArrayList(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return BulkTest.makeSuite(TestFastArrayList.class);
    }

    public static void main(String args[])
    {
        String[] testCaseName = { TestFastArrayList.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public void setUp()
    {
        list = (ArrayList) makeEmptyList();
    }

    public List makeEmptyList()
    {
        FastArrayList fal = new FastArrayList();
        fal.setFast(false);
        return (fal);
    }

}
