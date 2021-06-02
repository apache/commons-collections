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
package org.apache.commons.collections4;

import org.apache.commons.collections4.iterators.PairedIterator.PairedItem;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@RunWith(Enclosed.class)
public final class PairedIterableTest {

  private static final int SMALL_LIST_SIZE = 20;
  private static final int LARGE_LIST_SIZE = 40;

  private static final List<String> SMALL_STRINGS_LIST =
      unmodifiableList(stringsList(SMALL_LIST_SIZE));
  private static final List<String> LARGE_STRINGS_LIST =
      unmodifiableList(stringsList(LARGE_LIST_SIZE));
  private static final List<Integer> SMALL_INTS_LIST = unmodifiableList(intsList(SMALL_LIST_SIZE));
  private static final List<Integer> LARGE_INTS_LIST = unmodifiableList(intsList(LARGE_LIST_SIZE));

  @RunWith(Parameterized.class)
  public static final class ParameterizedTests<L, R> {
    private final String testCondition;
    private final List<L> leftList;
    private final List<R> rightList;
    private final int expectedIterableSize;

    public ParameterizedTests(
        String testCondition, List<L> leftList, List<R> rightList, int expectedIterableSize) {
      this.testCondition = testCondition;
      this.leftList = leftList;
      this.rightList = rightList;
      this.expectedIterableSize = expectedIterableSize;
    }

    @Test
    public void testAllowsForEach() {
      ArrayList<PairedItem<L, R>> outputPairedIterable = new ArrayList<>();
      for (PairedItem<L, R> item : PairedIterable.of(leftList, rightList)) {
        outputPairedIterable.add(item);
      }

      assertEquals(expectedIterableSize, outputPairedIterable.size());

      for (int i = 0; i < outputPairedIterable.size(); i++) {
        PairedItem<L, R> item = outputPairedIterable.get(i);
        assertEquals(leftList.get(i), item.getLeftItem());
        assertEquals(rightList.get(i), item.getRightItem());
      }
    }

    @Test
    public void testStream() {
      PairedIterable<L, R> testIterable = PairedIterable.of(leftList, rightList);

      assertEquals(
          leftList.subList(0, expectedIterableSize),
          testIterable.stream().map(PairedItem::getLeftItem).collect(toList()));
      assertEquals(
          rightList.subList(0, expectedIterableSize),
          testIterable.stream().map(PairedItem::getRightItem).collect(toList()));
    }

    @Parameters(name = "{0}")
    public static Object[][] testingParameters() {
      return new Object[][] {
        new Object[] {
          "left iterable (int) larger than right iterable (string)",
          LARGE_INTS_LIST,
          SMALL_STRINGS_LIST,
          SMALL_LIST_SIZE
        },
        new Object[] {
          "left iterable (string) larger than right iterable (int)",
          LARGE_STRINGS_LIST,
          SMALL_INTS_LIST,
          SMALL_LIST_SIZE
        },
        new Object[] {
          "equal sized left and right (int, string)",
          LARGE_INTS_LIST,
          LARGE_STRINGS_LIST,
          LARGE_LIST_SIZE
        },
        new Object[] {
          "equal sized left and right (string, int)",
          SMALL_STRINGS_LIST,
          SMALL_INTS_LIST,
          SMALL_LIST_SIZE
        },
        new Object[] {
          "Left empty, right small list", Collections.<Integer>emptyList(), LARGE_STRINGS_LIST, 0
        },
        new Object[] {
          "Right empty, left small list", LARGE_STRINGS_LIST, Collections.<Integer>emptyList(), 0
        },
        new Object[] {
          "Right and left both empty lists", Collections.emptyList(), Collections.emptyList(), 0
        },
      };
    }
  }

  @RunWith(JUnit4.class)
  public static final class ErrorTest {

    @Test
    public void leftIterableNullThrowsException() {
      assertThrows(NullPointerException.class, () -> PairedIterable.of(null, SMALL_INTS_LIST));
    }

    @Test
    public void rightIterableNullThrowsException() {
      assertThrows(NullPointerException.class, () -> PairedIterable.of(SMALL_INTS_LIST, null));
    }
  }

  private static List<String> stringsList(int size) {
    return IntStream.range(0, size)
        .boxed()
        .map(x -> UUID.randomUUID().toString())
        .collect(toList());
  }

  private static List<Integer> intsList(int size) {
    Random rnd = new Random();
    return IntStream.range(0, size).boxed().map(x -> rnd.nextInt()).collect(toList());
  }
}
