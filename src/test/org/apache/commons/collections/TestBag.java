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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Tests base {@link Bag} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeBag} method.
 * <p>
 * If your {@link Bag} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Bag} fails.
 *
 * @author Chuck Burdick
 * @version $Id: TestBag.java,v 1.5.2.1 2004/05/22 12:14:05 scolebourne Exp $
 */
// TODO: this class should really extend from TestCollection, but the bag
// implementations currently do not conform to the Collection interface.  Once
// those are fixed or at least a strategy is made for resolving the issue, this
// can be changed back to extend TestCollection instead.
public abstract class TestBag extends TestObject {
    public TestBag(String testName) {
        super(testName);
    }

    /**
     * Return a new, empty {@link Bag} to used for testing.
     */
    public abstract Bag makeBag();

    public Object makeObject() {
        return makeBag();
    }

    public void testBagAdd() {
        Bag bag = makeBag();
        bag.add("A");
        assertTrue("Should contain 'A'", bag.contains("A"));
        assertEquals("Should have count of 1",
                     1, bag.getCount("A"));
        bag.add("A");
        assertTrue("Should contain 'A'", bag.contains("A"));
        assertEquals("Should have count of 2",
                     2, bag.getCount("A"));
        bag.add("B");
        assertTrue(bag.contains("A"));
        assertTrue(bag.contains("B"));
    }

    public void testBagEqualsSelf() {
        Bag bag = makeBag();
        assertTrue(bag.equals(bag));
        bag.add("elt");
        assertTrue(bag.equals(bag));
        bag.add("elt"); // again
        assertTrue(bag.equals(bag));
        bag.add("elt2");
        assertTrue(bag.equals(bag));
    }

   public void testRemove() {
      Bag bag = makeBag();
      bag.add("A");
      assertEquals("Should have count of 1", 1, bag.getCount("A"));
      bag.remove("A");
      assertEquals("Should have count of 0", 0, bag.getCount("A"));
      bag.add("A");
      bag.add("A");
      bag.add("A");
      bag.add("A");
      assertEquals("Should have count of 4", 4, bag.getCount("A"));
      bag.remove("A", 0);
      assertEquals("Should have count of 4", 4, bag.getCount("A"));
      bag.remove("A", 2);
      assertEquals("Should have count of 2", 2, bag.getCount("A"));
      bag.remove("A");
      assertEquals("Should have count of 0", 0, bag.getCount("A"));
   }

   public void testRemoveAll() {
      Bag bag = makeBag();
      bag.add("A", 2);
      assertEquals("Should have count of 2", 2, bag.getCount("A"));
      bag.add("B");
      bag.add("C");
      assertEquals("Should have count of 4", 4, bag.size());
      List delete = new ArrayList();
      delete.add("A");
      delete.add("B");
      bag.removeAll(delete);
      assertEquals("Should have count of 1", 1, bag.getCount("A"));
      assertEquals("Should have count of 0", 0, bag.getCount("B"));
      assertEquals("Should have count of 1", 1, bag.getCount("C"));
      assertEquals("Should have count of 2", 2, bag.size());
   }

   public void testContains() {
      Bag bag = makeBag();
      bag.add("A");
      bag.add("A");
      bag.add("A");
      bag.add("B");
      bag.add("B");
      List compare = new ArrayList();
      compare.add("A");
      compare.add("B");
      assertEquals("Other list has 1 'B'", 1,
                 (new HashBag(compare)).getCount("B"));
      assertTrue("Bag has at least 1 'B'", 1 <= bag.getCount("B"));
      assertTrue("Bag contains items in the list", bag.containsAll(compare));
      compare.add("A");
      compare.add("B");
      assertEquals("Other list has 2 'B'", 2,
                 (new HashBag(compare)).getCount("B"));
      assertTrue("Bag has at least 2 'B'", 2 <= bag.getCount("B"));
      assertTrue("Bag contains items in the list", bag.containsAll(compare));
      compare.add("A");
      compare.add("B");
      assertEquals("Other list has 3 'B'", 3,
                 (new HashBag(compare)).getCount("B"));
      assertTrue("Bag does not have 3 'B'", 3 > bag.getCount("B"));
      assertTrue("Bag contains items in the list", !bag.containsAll(compare));
   }

   public void testSize() {
      Bag bag = makeBag();
      bag.add("A");
      bag.add("A");
      bag.add("A");
      bag.add("B");
      bag.add("B");
      assertEquals("Should have 5 total items", 5, bag.size());
      bag.remove("A", 2);
      assertEquals("Should have 1 'A'", 1, bag.getCount("A"));
      assertEquals("Should have 3 total items", 3, bag.size());
      bag.remove("B");
      assertEquals("Should have 1 total item", 1, bag.size());
   }

   public void testRetainAll() {
      Bag bag = makeBag();
      bag.add("A");
      bag.add("A");
      bag.add("A");
      bag.add("B");
      bag.add("B");
      bag.add("C");
      List retains = new ArrayList();
      retains.add("B");
      retains.add("C");
      bag.retainAll(retains);
      assertEquals("Should have 2 total items", 2, bag.size());
   }

   public void testIterator() {
      Bag bag = makeBag();
      bag.add("A");
      bag.add("A");
      bag.add("B");
      assertEquals("Bag should have 3 items", 3, bag.size());
      Iterator i = bag.iterator();

      boolean foundA = false;
      while (i.hasNext()) {
          String element = (String) i.next();
          // ignore the first A, remove the second via Iterator.remove()
          if (element.equals("A")) {
              if (foundA == false) {
                  foundA = true;
              } else {
                  i.remove();
              }
          }
      }

      assertTrue("Bag should still contain 'A'", bag.contains("A"));
      assertEquals("Bag should have 2 items", 2, bag.size());
      assertEquals("Bag should have 1 'A'", 1, bag.getCount("A"));
   }

   public void testIteratorFail() {
      Bag bag = makeBag();
      bag.add("A");
      bag.add("A");
      bag.add("B");
      Iterator i = bag.iterator();
      i.next();
      bag.remove("A");
      try {
         i.next();
         fail("Should throw ConcurrentModificationException");
      } catch (ConcurrentModificationException e) {
         // expected
      }
   }
}


