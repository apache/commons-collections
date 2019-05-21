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
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test suite for {@link PairingIterator}.
 *
 */
public class PairingIteratorTest extends AbstractIteratorTest<Pair<String, String>> {

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
		final List<String> rightList = Arrays.asList(new String[] { "A" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(null, rightList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Pair<String, String> next = pairingIterator.next();
		Assert.assertEquals(null, next.getLeft());
		Assert.assertEquals("A", next.getRight());
	}

	@Test
	public void testNullValueSecond() {
		final List<String> leftList = Arrays.asList(new String[] { "A" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(leftList.iterator(), null);

		Assert.assertTrue(pairingIterator.hasNext());

		Pair<String, String> next = pairingIterator.next();
		Assert.assertEquals("A", next.getLeft());
		Assert.assertEquals(null, next.getRight());
	}

	@Test
	public void testTwoIteratorsWithBothTwoElements() {
		final List<String> leftList = Arrays.asList(new String[] { "A1", "A2" });
		final List<String> rightList = Arrays.asList(new String[] { "B1", "B2" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(leftList.iterator(),
				rightList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Pair<String, String> next = pairingIterator.next();
		Assert.assertEquals("A1", next.getLeft());
		Assert.assertEquals("B1", next.getRight());

		Assert.assertTrue(pairingIterator.hasNext());
		next = pairingIterator.next();
		Assert.assertEquals("A2", next.getLeft());
		Assert.assertEquals("B2", next.getRight());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testTwoIteratorsWithDifferentSize() {
		final List<String> leftList = Arrays.asList(new String[] { "A1", "A2", "A3" });
		final List<String> rightList = Arrays.asList(new String[] { "B1", "B2" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(leftList.iterator(),
				rightList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Pair<String, String> next = pairingIterator.next();
		Assert.assertEquals("A1", next.getLeft());
		Assert.assertEquals("B1", next.getRight());

		Assert.assertTrue(pairingIterator.hasNext());

		next = pairingIterator.next();
		Assert.assertEquals("A2", next.getLeft());
		Assert.assertEquals("B2", next.getRight());

		Assert.assertTrue(pairingIterator.hasNext());
		next = pairingIterator.next();
		Assert.assertEquals("A3", next.getLeft());
		Assert.assertEquals(null, next.getRight());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testTwoIteratorsWithDifferentSize2() {
		final List<String> leftList = Arrays.asList(new String[] { "A1", });
		final List<String> rightList = Arrays.asList(new String[] { "B1", "B2" });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(leftList.iterator(),
				rightList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Pair<String, String> next = pairingIterator.next();
		Assert.assertEquals("A1", next.getLeft());
		Assert.assertEquals("B1", next.getRight());

		Assert.assertTrue(pairingIterator.hasNext());

		next = pairingIterator.next();
		Assert.assertEquals(null, next.getLeft());
		Assert.assertEquals("B2", next.getRight());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testTwoIteratorsWithNullValues() {
		final List<String> leftList = Arrays.asList(new String[] { null, "A2" });
		final List<String> rightList = Arrays.asList(new String[] { "B1", null });
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(leftList.iterator(),
				rightList.iterator());

		Assert.assertTrue(pairingIterator.hasNext());

		Pair<String, String> next = pairingIterator.next();
		Assert.assertEquals(null, next.getLeft());
		Assert.assertEquals("B1", next.getRight());

		Assert.assertTrue(pairingIterator.hasNext());
		next = pairingIterator.next();
		Assert.assertEquals("A2", next.getLeft());
		Assert.assertEquals(null, next.getRight());

		Assert.assertFalse(pairingIterator.hasNext());
	}

	@Test
	public void testRemoveWithOneNullIterator() {
		final List<String> leftList = new ArrayList<>();
		leftList.add("A1");
		final Iterator<String> leftIterator = leftList.iterator();
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(leftIterator, null);

		pairingIterator.next();
		pairingIterator.remove();
		Assert.assertTrue(leftList.isEmpty());
	}
	
	@Test
	public void testRemoveWithOneNullIterator2() {
		final List<String> rightList = new ArrayList<>();
		rightList.add("A1");
		final Iterator<String> rightIterator = rightList.iterator();
		final PairingIterator<String, String> pairingIterator = new PairingIterator<>(null, rightIterator);

		pairingIterator.next();
		pairingIterator.remove();
		Assert.assertTrue(rightList.isEmpty());
	}

	@Override
	public Iterator<Pair<String, String>> makeEmptyIterator() {
		return new PairingIterator<String, String>(IteratorUtils.<String>emptyIterator(),
				IteratorUtils.<String>emptyIterator());
	}

	@Override
	public Iterator<Pair<String, String>> makeObject() {
		final List<String> leftList = new ArrayList<>();
		leftList.add("A1");
		leftList.add("A2");
		leftList.add("A3");
		final List<String> rightList = new ArrayList<>();
		rightList.add("B1");
		rightList.add("B2");
		return new PairingIterator<>(leftList.iterator(), rightList.iterator());
	}

}
