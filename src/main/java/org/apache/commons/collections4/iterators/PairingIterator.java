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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Provides an iteration over the elements of two iterators. It will be return a
 * {@link Pair} with the elements of the iterators.
 * <p>
 * Given two {@link Iterator} instances {@code A} and {@code B}. The
 * {@link #next()} method will return a {@link Pair} with the elements of the
 * {@code A} and {@code B} until both iterators are exhausted.
 * </p>
 * <p>
 * If one of the iterators is null, the result {@link Pair} has also a null
 * value and the value of the other iterator.
 * </p>
 * <p>
 * If one iterator has more elements then the other, the result {@link Pair}
 * will contain null values if one of the iterator exhausted.
 *
 * @param <L>
 *            type of left value of {@link Pair}
 * 
 * @param <R>
 *            type of the right value of {@link Pair}
 * 
 * @since 4.4
 */
public class PairingIterator<L, R> implements Iterator<Pair<L, R>> {

	private final Iterator<L> firstIterator;
	private final Iterator<R> secondIterator;

	/**
	 * Constructs a new <code>PairingIterator</code> that will provide a
	 * {@link} Pair of the child iterator elements.
	 * 
	 * @param firstIterator
	 *            the first iterator
	 * @param secondIterator
	 *            the second iterator
	 */
	public PairingIterator(Iterator<L> firstIterator, Iterator<R> secondIterator) {
		this.firstIterator = firstIterator;
		this.secondIterator = secondIterator;
	}

	/**
	 * Returns {@code true} if one of the child iterators has remaining elements.
	 */
	@Override
	public boolean hasNext() {
		return (null != firstIterator && firstIterator.hasNext())
				|| (null != secondIterator && secondIterator.hasNext());
	}

	/**
	 * Returns the next {@link Pair} with the next values of the child iterators.
	 * 
	 * @return a {@link Pair} with the next values of child iterators
	 * @throws NoSuchElementException
	 *             if no child iterator has any more elements
	 */
	@Override
	public Pair<L, R> next() throws NoSuchElementException {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final L leftValue = null != firstIterator && firstIterator.hasNext() ? firstIterator.next() : null;
		final R rightValue = null != secondIterator && secondIterator.hasNext() ? secondIterator.next() : null;
		return Pair.of(leftValue, rightValue);
	}

	/**
	 * Removes the last returned values of the child iterators.
	 */
	@Override
	public void remove() {
		if (firstIterator != null) {
			firstIterator.remove();
		}
		if (secondIterator != null) {
			secondIterator.remove();
		}
	}

}
