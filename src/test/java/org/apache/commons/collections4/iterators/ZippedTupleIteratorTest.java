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
package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.iterators.ZippedTupleIterator.ZippedTuple;


/** Unit test suite for {@link ZippingIterator}. */
public final class ZippedTupleIteratorTest
    extends AbstractIteratorTest<ZippedTuple<String, Integer>> {

  //------------------------------------------------------------ Conventional

  public ZippedTupleIteratorTest(String testName) {
    super(testName);
  }

  //------------------------------------------------------------ Lifecycle

  private ArrayList<String> smallStringsList = null;
  private ArrayList<String> largeStringsList = null;
  private ArrayList<Integer> smallIntsList = null;
  private ArrayList<Integer> largeIntsList = null;

  // Unequal sized lists
  private static final int SMALL_LIST_SIZE = 20;
  private static final int LARGE_LIST_SIZE = 40;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    smallStringsList = new ArrayList<>();
    largeStringsList = new ArrayList<>();
    smallIntsList = new ArrayList<>();
    largeIntsList = new ArrayList<>();

    Random random = new Random();

    for (int i = 0; i < SMALL_LIST_SIZE; i++) {
      smallIntsList.add(random.nextInt());
      smallStringsList.add(UUID.randomUUID().toString());
    }

    for (int i = 0; i < LARGE_LIST_SIZE; i++) {
      largeIntsList.add(random.nextInt());
      largeStringsList.add(UUID.randomUUID().toString());
    }
  }

  @Override
  public boolean supportsRemove() {
    return false;
  }

  //---------------------------------------------------- TestIterator Methods

  @Override
  public Iterator<ZippedTuple<String, Integer>> makeEmptyIterator() {
    return ZippedTupleIterator.of(IteratorUtils.emptyIterator(), IteratorUtils.emptyIterator());
  }

  @Override
  public Iterator<ZippedTuple<String, Integer>> makeObject() {
    return ZippedTupleIterator.of(smallStringsList.iterator(), smallIntsList.iterator());
  }

  public void testLeftIteratorLargerThanRight() {
    Iterator<ZippedTuple<String, Integer>> zipPairIterator =
        ZippedTupleIterator.ofIterables(largeStringsList, smallIntsList);


    for (int i = 0; i < SMALL_LIST_SIZE; i++) {
      assertTrue(zipPairIterator.hasNext());
      ZippedTuple<String, Integer> zippedItem = zipPairIterator.next();

      assertEquals(largeStringsList.get(i) , zippedItem.getLeftItem());
      assertEquals(smallIntsList.get(i) , zippedItem.getRightItem());
    }

    assertFalse(zipPairIterator.hasNext());
  }

  public void testRightIteratorLargerThanLeft() {
    Iterator<ZippedTuple<String, Integer>> zipPairIterator =
        ZippedTupleIterator.ofIterables(smallStringsList, largeIntsList);


    for (int i = 0; i < SMALL_LIST_SIZE; i++) {
      assertTrue(zipPairIterator.hasNext());
      ZippedTuple<String, Integer> zippedItem = zipPairIterator.next();

      assertEquals(smallStringsList.get(i) , zippedItem.getLeftItem());
      assertEquals(largeIntsList.get(i) , zippedItem.getRightItem());
    }

    assertFalse(zipPairIterator.hasNext());
  }

  public void testEmptyLeftIterator() {
    Iterator<ZippedTuple<String, Integer>> zipPairIterator =
        ZippedTupleIterator.of(IteratorUtils.emptyIterator(), largeIntsList.iterator());

    assertFalse(zipPairIterator.hasNext());
  }

  public void testEmptyRightIterator() {
    Iterator<ZippedTuple<String, Integer>> zipPairIterator =
        ZippedTupleIterator.of(largeStringsList.iterator(), IteratorUtils.emptyIterator());

    assertFalse(zipPairIterator.hasNext());
  }


  public void testValidTupleString() {
    Iterator<ZippedTuple<String, Integer>> zipPairIterator =
        ZippedTupleIterator.ofIterables(smallStringsList, largeIntsList);


    for (int i = 0; i < SMALL_LIST_SIZE; i++) {
      assertTrue(zipPairIterator.hasNext());
      ZippedTuple<String, Integer> zippedItem = zipPairIterator.next();

      assertEquals(
          String.format("{%s, %s}", zippedItem.getLeftItem(), zippedItem.getRightItem()),
          zippedItem.toString());
    }

    assertFalse(zipPairIterator.hasNext());
  }
}