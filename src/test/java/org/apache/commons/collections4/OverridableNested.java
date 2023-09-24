package org.apache.commons.collections4;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom version of {@code @Nested} that is used to signal that the annotated class is a nested,
 * non-static test class (i.e., an <em>inner class</em>).
 * <p>
 * However in this version subclasses of the containing class can override the nested class
 * such that only the most derived version is run. Each member of the hierachy should have the same name and
 * also have the OverridableNestedTests annotation.
 *
 * {@see Nested}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Nested
@ExtendWith(OverridableNested.NestedTestCondition.class)
public @interface OverridableNested {
    String baseName() default "";

    class NestedTestCondition implements ExecutionCondition {
        @Override
        public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext extensionContext) {
            final Class<?> annotatedType = extensionContext.getRequiredTestClass();
            if (extensionContext.getTestInstances().isPresent()) {
                return checkInstances(annotatedType, extensionContext.getTestInstances().get());
            } else {
                if (annotatedType.isAnnotationPresent(Nested.class)) {
                    throw new ExtensionConfigurationException("@OverridableNested on " + annotatedType.getName()
                            + " but found conflicting @Nested annotation");
                } else if (annotatedType.getEnclosingClass() == null) {
                    return ConditionEvaluationResult.disabled("@OverridableNested on " + extensionContext.getDisplayName()
                            + " but no enclosing class found");
                } else {
                    return ConditionEvaluationResult.enabled("allow until we have instance of a runtime type to check");
                }
            }
        }

        private static ConditionEvaluationResult checkInstances(final Class<?> annotatedType, final TestInstances testInstances) {
            final List<Object> enclosingInstanceList = testInstances.getEnclosingInstances();
            if (enclosingInstanceList.isEmpty()) {
                return ConditionEvaluationResult.disabled("@OverridableNested on " + annotatedType.getName()
                        + " but no enclosing instance found");
            }

            final Object immediateEnclosingInstance = enclosingInstanceList.get(enclosingInstanceList.size() - 1);
            final List<Class<?>> similarClasses = findSimilarNestedClasses(immediateEnclosingInstance.getClass(), annotatedType);
            if (similarClasses.isEmpty()) {
                throw new ExtensionConfigurationException("@OverridableNested on " + annotatedType.getName() + " found no matching classes");
            }

            if (annotatedType.equals(similarClasses.get(0))) {
                return ConditionEvaluationResult.enabled("allow");
            } else {
                return ConditionEvaluationResult.disabled("overridden by " + similarClasses.get(0).getName());
            }
        }

        // will list most derived version first
        private static List<Class<?>> findSimilarNestedClasses(final Class<?> instanceType, final Class<?> annotatedType) {
            final String compareName = getCompareName(annotatedType);
            final List<Class<?>> list = new ArrayList<>();
            Class<?> currentType = instanceType;
            do {
                for (final Class<?> innerClass : currentType.getDeclaredClasses()) {
                    final String name = getCompareName(innerClass);
                    if (compareName.equals(name)) {
                        if (!innerClass.isAnnotationPresent(OverridableNested.class)) {
                            throw new ExtensionConfigurationException("@OverridableNested on " + annotatedType.getName()
                                    + " but missing from " + innerClass.getName());
                        }
                        list.add(innerClass);
                    }
                }
                currentType = currentType.getSuperclass();
            } while (currentType != null);
            return list;
        }

        private static String getCompareName(final Class<?> type) {
            final OverridableNested annotation = type.getAnnotation(OverridableNested.class);
            if (annotation != null && StringUtils.isNotEmpty(annotation.baseName())) {
                return annotation.baseName();
            } else {
                return type.getSimpleName();
            }
        }
    }
}
