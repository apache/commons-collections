/*
 *  Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Extension of {@link TestList} for exercising the {@link FixedSizeList}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/02/18 01:20:34 $
 * 
 * @author Stephen Colebourne
 */
public class TestFixedSizeList extends AbstractTestList {

    public TestFixedSizeList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFixedSizeList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestFixedSizeList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public List makeEmptyList() {
        return FixedSizeList.decorate(new ArrayList());
    }

    public List makeFullList() {
        List list = new ArrayList();
        list.addAll(Arrays.asList(getFullElements()));
        return FixedSizeList.decorate(list);
    }
    
    public boolean isAddSupported() {
        return false;
    }

    public boolean isRemoveSupported() {
        return false;
    }

}
