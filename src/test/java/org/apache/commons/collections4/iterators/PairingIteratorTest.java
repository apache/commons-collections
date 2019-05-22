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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.iterators.PairingIterator.Entry;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test suite for {@link PairingIterator}.
 *
 */
public class PairingIteratorTest extends AbstractIteratorTest<Entry<String, String>> {

	public PairingIteratorTest(String testName) {
		super(testName);
	}

	@Test
	public void testNullValuesBoth() {
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(null, null);

		Assert.assertFalse(pairingIterator.hasNext());
		try {
			pairingIterator.next();
			fail();

		} catch (NoSuchElementException e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void testNullValueFirst() {
		final List<String> firstList = Arrays.asList(new String[] { "A" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(null, firstList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Entry<String, String> next = pairingIterator.next();
		Assert.assertEquals(null, next.getFirstValue());
		Assert.assertEquals("A", next.getSecondValue());
	}

	@Test
	public void testNullValueSecond() {
		final List<String> firstList = Arrays.asList(new String[] { "A" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(firstList.iterator(), null);

		Assert.assertTrue(pairingIterator.hasNext());

		Entry<String, String> next = pairingIterator.next();
		Assert.assertEquals("A", next.getFirstValue());
		Assert.assertEquals(null, next.getSecondValue());
	}

	@Test
	public void testTwoIteratorsWithBothTwoElements() {
		final List<String> firstList = Arrays.asList(new String[] { "A1", "A2" });
		final List<String> secondList = Arrays.asList(new String[] { "B1", "B2" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(firstList.iterator(),
				secondList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Entry<String, String> next = pairingIterator.next();
		Assert.assertEquals("A1", next.getFirstValue());
		Assert.assertEquals("B1", next.getSecondValue());

		Assert.assertTrue(pairingIterator.hasNext());
		next = pairingIterator.next();
		Assert.assertEquals("A2", next.getFirstValue());
		Assert.assertEquals("B2", next.getSecondValue());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testTwoIteratorsWithDifferentSize() {
		final List<String> firstList = Arrays.asList(new String[] { "A1", "A2", "A3" });
		final List<String> secondList = Arrays.asList(new String[] { "B1", "B2" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(firstList.iterator(),
				secondList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Entry<String, String> next = pairingIterator.next();
		Assert.assertEquals("A1", next.getFirstValue());
		Assert.assertEquals("B1", next.getSecondValue());

		Assert.assertTrue(pairingIterator.hasNext());

		next = pairingIterator.next();
		Assert.assertEquals("A2", next.getFirstValue());
		Assert.assertEquals("B2", next.getSecondValue());

		Assert.assertTrue(pairingIterator.hasNext());
		next = pairingIterator.next();
		Assert.assertEquals("A3", next.getFirstValue());
		Assert.assertEquals(null, next.getSecondValue());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testTwoIteratorsWithDifferentSize2() {
		final List<String> firstList = Arrays.asList(new String[] { "A1", });
		final List<String> secondList = Arrays.asList(new String[] { "B1", "B2" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(firstList.iterator(),
				secondList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Entry<String, String> next = pairingIterator.next();
		Assert.assertEquals("A1", next.getFirstValue());
		Assert.assertEquals("B1", next.getSecondValue());

		Assert.assertTrue(pairingIterator.hasNext());

		next = pairingIterator.next();
		Assert.assertEquals(null, next.getFirstValue());
		Assert.assertEquals("B2", next.getSecondValue());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testTwoIteratorsWithNullValues() {
		final List<String> firstList = Arrays.asList(new String[] { null, "A2" });
		final List<String> secondList = Arrays.asList(new String[] { "B1", null });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(firstList.iterator(),
				secondList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Entry<String, String> next = pairingIterator.next();
		Assert.assertEquals(null, next.getFirstValue());
		Assert.assertEquals("B1", next.getSecondValue());

		Assert.assertTrue(pairingIterator.hasNext());
		next = pairingIterator.next();
		Assert.assertEquals("A2", next.getFirstValue());
		Assert.assertEquals(null, next.getSecondValue());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testRemoveWithOneNullIterator() {
		final List<String> firstList = new ArrayList<>();
		firstList.add("A1");
		final Iterator<String> leftIterator = firstList.iterator();
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(leftIterator, null);

		pairingIterator.next();
		pairingIterator.remove();
		Assert.assertTrue(firstList.isEmpty());
	}

	@Test
	public void testRemoveWithOneNullIterator2() {
		final List<String> secondList = new ArrayList<>();
		secondList.add("A1");
		final Iterator<String> rightIterator = secondList.iterator();
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(null, rightIterator);

		pairingIterator.next();
		pairingIterator.remove();
		Assert.assertTrue(secondList.isEmpty());
	}

	@Test
	public void testEntryEquals_notEquals() {
		final String firstValue = "A";
		final String secondValue = "B";
		final Entry<String, String> entry1 = new Entry<>(firstValue, secondValue);
		final Entry<String, String> entry2 = new Entry<>(secondValue, firstValue);

		Assert.assertNotEquals(entry1, entry2);
	}

	@Test
	public void testEntryEquals_equals() {
		final String firstValue = "A";
		final String secondValue = "A";
		final Entry<String, String> entry1 = new Entry<>(firstValue, secondValue);
		final Entry<String, String> entry2 = new Entry<>(secondValue, firstValue);

		Assert.assertEquals(entry1, entry2);
	}

	@Test
	public void testEntryEquals_equalsSameInstance() {
		final String firstValue = "A";
		final String secondValue = "A";
		final Entry<String, String> entry1 = new Entry<>(firstValue, secondValue);

		Assert.assertEquals(entry1, entry1);
	}

	@Override
	public Iterator<Entry<String, String>> makeEmptyIterator() {
		return new PairingIterator<String, String>(IteratorUtils.<String>emptyIterator(),
				IteratorUtils.<String>emptyIterator());
	}

	@Override
	public Iterator<Entry<String, String>> makeObject() {
		final List<String> firstList = new ArrayList<>();
		firstList.add("A1");
		firstList.add("A2");
		firstList.add("A3");
		final List<String> secondList = new ArrayList<>();
		secondList.add("B1");
		secondList.add("B2");
		return new PairingIterator<>(firstList.iterator(), secondList.iterator());
	}

}
