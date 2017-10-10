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
/**
 * This package contains implementations of the {@link java.util.Set Set},
 * {@link java.util.SortedSet SortedSet} and
 * {@link java.util.NavigableSet NavigableSet} interfaces.
 * <p>
 * The implementations are in the form of direct implementations and decorators.
 * A decorator wraps another implementation of the interface to add some
 * specific additional functionality.
 * <p>
 * The following implementations are provided in the package:
 * <ul>
 *   <li>CompositeSet - a set that combines multiple sets into one
 * </ul>
 * The following decorators are provided in the package:
 * <ul>
 *   <li>Unmodifiable - ensures the collection cannot be altered
 *   <li>Predicated - ensures that only elements that are valid according to a predicate can be added
 *   <li>Transformed - transforms each element added
 *   <li>ListOrdered - ensures that insertion order is retained
 *   <li>MapBackedSet - a set formed by decorating a Map
 * </ul>
 *
 */
package org.apache.commons.collections4.set;
