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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.bag.AbstractTestBag;

/**
 * Extension of {@link TestBag} for exercising the {@link TreeBag}
 * implementation.
 * 
 * @version $Revision: 1.10 $ $Date: 2004/02/18 01:20:35 $
 *
 * @author Chuck Burdick
 */
public class TestTreeBag extends AbstractTestBag {
    
   public TestTreeBag(String testName) {
      super(testName);
   }

   public static Test suite() {
      return new TestSuite(TestTreeBag.class);
   }

   public static void main(String args[]) {
      String[] testCaseName = { TestTreeBag.class.getName() };
      junit.textui.TestRunner.main(testCaseName);
   }

   public Bag makeBag() {
      return new TreeBag();
   }

   public SortedBag setupBag() {
      SortedBag bag = (SortedBag)makeBag();
      bag.add("C");
      bag.add("A");
      bag.add("B");
      bag.add("D");
      return bag;
   }

   public void testOrdering() {
      Bag bag = setupBag();
      assertEquals("Should get elements in correct order",
                   "A", bag.toArray()[0]);
      assertEquals("Should get elements in correct order",
                   "B", bag.toArray()[1]);
      assertEquals("Should get elements in correct order",
                   "C", bag.toArray()[2]);
      assertEquals("Should get first key",
                   "A", ((SortedBag)bag).first());
      assertEquals("Should get last key",
                   "D", ((SortedBag)bag).last());
   }
}
