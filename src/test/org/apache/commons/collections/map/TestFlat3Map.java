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
package org.apache.commons.collections.map;

import java.util.Map;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.iterators.AbstractTestMapIterator;

/**
 * JUnit tests.
 * 
 * @version $Revision: 1.6 $ $Date: 2004/02/18 01:20:37 $
 * 
 * @author Stephen Colebourne
 */
public class TestFlat3Map extends AbstractTestIterableMap {

    public TestFlat3Map(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestFlat3Map.class);
    }

    public Map makeEmptyMap() {
        return new Flat3Map();
    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestMapIterator() {
        return new TestFlatMapIterator();
    }
    
    public class TestFlatMapIterator extends AbstractTestMapIterator {
        public TestFlatMapIterator() {
            super("TestFlatMapIterator");
        }
        
        public Object[] addSetValues() {
            return TestFlat3Map.this.getNewSampleValues();
        }
        
        public boolean supportsRemove() {
            return TestFlat3Map.this.isRemoveSupported();
        }

        public boolean supportsSetValue() {
            return TestFlat3Map.this.isSetValueSupported();
        }

        public MapIterator makeEmptyMapIterator() {
            resetEmpty();
            return ((Flat3Map) TestFlat3Map.this.map).mapIterator();
        }

        public MapIterator makeFullMapIterator() {
            resetFull();
            return ((Flat3Map) TestFlat3Map.this.map).mapIterator();
        }
        
        public Map getMap() {
            // assumes makeFullMapIterator() called first
            return TestFlat3Map.this.map;
        }
        
        public Map getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return TestFlat3Map.this.confirmed;
        }
        
        public void verify() {
            super.verify();
            TestFlat3Map.this.verify();
        }
    }
}
