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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom version of {@link Nested} that is used to signal that the annotated class is a nested,
 * non-static test class (i.e., an <em>inner class</em>).
 * <p>
 * However in this version subclasses of the containing class can override the nested class
 * such that only the most derived version is run.
 * The primary implementation must have the {@link NestedOverridable} annotation,
 * while subclasses need to reference it using {@link NestedOverride}.
 *
 * @see Nested
 * @see NestedOverride
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Nested
@ExtendWith(NestedOverrideCondition.class)
public @interface NestedOverridable {
}
