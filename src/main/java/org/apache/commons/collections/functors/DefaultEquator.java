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
package org.apache.commons.collections.functors;

/**
 * Default {@link Equator} implementation.
 * 
 * @param <T>
 * @since Commons Collections 4.0
 * @version $Revision$ $Date$
 */
public class DefaultEquator<T> implements Equator<T> {
	/** Static instance */
	public static final DefaultEquator<Object> INSTANCE = new DefaultEquator<Object>();

	/**
	 * Hashcode used for <code>null</code> objects.
	 */
	public static final int HASHCODE_NULL = -1;

	/**
	 * {@inheritDoc} Delegates to {@link Object#equals(Object)}.
	 */
	public boolean equate(T o1, T o2) {
		return o1 == o2 || o1 != null && o1.equals(o2);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return <code>o.hashCode()</code> if <code>o</code> is non-
	 *         <code>null</code>, else {@link #HASHCODE_NULL}.
	 */
	public int hash(T o) {
		return o == null ? HASHCODE_NULL : o.hashCode();
	}

	private Object readResolve() {
		return INSTANCE;
	}

	/**
	 * Get a typed {@link DefaultEquator} instance.
	 * 
	 * @param <T>
	 * @return {@link DefaultEquator#INSTANCE}
	 */
	@SuppressWarnings("unchecked")
	public static <T> DefaultEquator<T> defaultEquator() {
		return (DefaultEquator<T>) DefaultEquator.INSTANCE;
	}
}
