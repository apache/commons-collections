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
 * This package contains implementations of the
 * {@link java.util.Collection Collection} interface.
 * <p>
 * The following implementations are provided in the package:
 * <ul>
 *   <li>CompositeCollection - a collection that combines multiple collections into one
 * </ul>
 * The following decorators are provided in the package:
 * <ul>
 *   <li>Synchronized - synchronizes method access for multi-threaded environments
 *   <li>Unmodifiable - ensures the collection cannot be altered
 *   <li>Predicated - ensures that only elements that are valid according to a predicate can be added
 *   <li>Transformed - transforms elements as they are added
 *   <li>Indexed - provides a map-like view onto another collection
 * </ul>
 *
 */
package org.apache.commons.collections4.collection;
