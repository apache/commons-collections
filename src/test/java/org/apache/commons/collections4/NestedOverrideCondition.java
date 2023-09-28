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
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;

import java.util.ArrayList;
import java.util.List;

/**
 * Support for {@link NestedOverridable} and {@link NestedOverride}.
 * <p>
 * Checks hierarchy of similar nested classes in the enclosing class and only allows the most derived version to run.
 */
public class NestedOverrideCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext extensionContext) {
        final Class<?> annotatedType = extensionContext.getRequiredTestClass();
        final String checkName = annotatedType.getName();
        final Class<?> baseRef = getBaseReference(annotatedType, false);

        if (extensionContext.getTestInstances().isPresent()) {
            final Class<?> mostDerived = findMostDerived(extensionContext.getTestInstances().get(), baseRef, checkName);
            if (annotatedType.equals(mostDerived)) {
                return ConditionEvaluationResult.enabled("allow");
            } else {
                return ConditionEvaluationResult.disabled("overridden by " + mostDerived.getName());
            }
        } else {
            return ConditionEvaluationResult.enabled("allow until we have instance of a runtime type to check");
        }
    }

    private static Class<?> findMostDerived(final TestInstances testInstances, final Class<?> baseRef, final String checkName) {
        final List<Object> enclosingInstanceList = testInstances.getEnclosingInstances();
        if (enclosingInstanceList.isEmpty()) {
            throw new ExtensionConfigurationException("@NestedOverridable/@NestedOverride on " + checkName
                    + " but no enclosing instance found");
        }

        // instances list starts with the furthest outer layer
        // it excludes the instance of the nested class itself
        // thus the last element should be the level the nested annotation is applied at we need to check
        final Object immediateEnclosingInstance = enclosingInstanceList.get(enclosingInstanceList.size() - 1);

        // search the override hierarchy, will list from the most derived layer first
        final List<Class<?>> similarClasses = findSimilarNestedClasses(immediateEnclosingInstance.getClass(), baseRef);
        if (similarClasses.isEmpty()) {
            throw new ExtensionConfigurationException("@NestedOverridable/@NestedOverride on " + checkName + " found no matching classes");
        } else if (!similarClasses.contains(baseRef)) {
            throw new ExtensionConfigurationException("@NestedOverridable/@NestedOverride on " + checkName
                    + " but didn't find referenced base class " + baseRef.getName() + " in same hierarchy");
        }
        return similarClasses.get(0);
    }

    private static List<Class<?>> findSimilarNestedClasses(final Class<?> instanceType, final Class<?> targetRef) {
        final List<Class<?>> nestedAlternates = new ArrayList<>();

        Class<?> currentType = instanceType;
        while (currentType != null) {
            for (final Class<?> innerClass : currentType.getDeclaredClasses()) {
                final Class<?> innerRef = getBaseReference(innerClass, true);
                if (targetRef.equals(innerRef)) {
                    nestedAlternates.add(innerClass);
                }
            }
            currentType = currentType.getSuperclass();
        }

        return nestedAlternates;
    }

    private static Class<?> getBaseReference(final Class<?> type, final boolean optional) {
        final NestedOverridable declare = type.getAnnotation(NestedOverridable.class);
        final NestedOverride override = type.getAnnotation(NestedOverride.class);
        if (declare == null && override == null) {
            if (optional) {
                return null;
            } else {
                throw new ExtensionConfigurationException("couldn't find expected @NestedOverridable/@NestedOverride on " + type.getName());
            }
        } else if (declare != null && override != null) {
            throw new ExtensionConfigurationException("found conflicting @NestedOverridable and @NestedOverride on " + type.getName());
        } else if (type.isAnnotationPresent(Nested.class)) {
            throw new ExtensionConfigurationException("found conflicting @NestedOverridable/@NestedOverride and @Nested on " + type.getName());
        } else if (declare != null) {
            return type;
        } else if (override.value() == null) {
            throw new ExtensionConfigurationException("for @NestedOverride on " + type.getName() + " base reference is not optional");
        } else if (!override.value().isAnnotationPresent(NestedOverridable.class)) {
            throw new ExtensionConfigurationException("@NestedOverride on " + type.getName() + " points to " + override.value() + " which is missing required @NestedOverridable");
        } else {
            return override.value();
        }
    }
}
