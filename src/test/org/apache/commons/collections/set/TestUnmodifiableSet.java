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
package org.apache.commons.collections.set;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;

/**
 * Extension of {@link AbstractTestSet} for exercising the 
 * {@link UnmodifiableSet} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2004/02/18 01:20:39 $
 * 
 * @author Phil Steitz
 */
public class TestUnmodifiableSet extends AbstractTestSet{
    
    public TestUnmodifiableSet(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestUnmodifiableSet.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestUnmodifiableSet.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    //-------------------------------------------------------------------  
    public Set makeEmptySet() {
        return UnmodifiableSet.decorate(new HashSet());
    }
    
    public Set makeFullSet() {
        HashSet set = new HashSet();
        set.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableSet.decorate(set);
    }
    
    public boolean isAddSupported() {
        return false;
    }
    
    public boolean isRemoveSupported() {
        return false;
    }
           
}
