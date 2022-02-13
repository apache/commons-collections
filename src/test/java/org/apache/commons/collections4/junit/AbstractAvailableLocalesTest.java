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

package org.apache.commons.collections4.junit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractAvailableLocalesTest {

//    public static List<Object[]> combine(final Object[] objects, final List<Locale> locales) {
//        final List<Object[]> result = new ArrayList<>(objects.length * locales.size());
//        Arrays.stream(objects).forEachOrdered(object -> locales.stream().forEachOrdered(locale -> result.add(new Object[] { object, locale })));
//        return result;
//    }
//
//    public static List<Object[]> combine(final Object[] objects, final Locale[] locales) {
//        final List<Object[]> result = new ArrayList<>(objects.length * locales.length);
//        Arrays.stream(objects).forEachOrdered(object -> Arrays.stream(locales).forEachOrdered(locale -> result.add(new Object[] { object, locale })));
//        return result;
//    }

//    public static List<Object[]> combineAvailableLocales(final Object[] objects) {
//        return combine(objects, getSortedAvailableLocales());
//    }
//
//    public static List<Object[]> combineDeclaredLocales(final Object[] objects) {
//        return combine(objects, getSortedDeclaredLocales());
//    }

    @Parameters(name = "{0}")
    public static Locale[] getSortedAvailableLocales() {
        final Locale[] availableLocales = Locale.getAvailableLocales();
        Arrays.sort(availableLocales, new ObjectToStringComparator());
        return availableLocales;
    }

    public static List<Locale> getSortedDeclaredLocales() {
        final Field[] allFields = FieldUtils.getAllFields(Locale.class);
        final List<Locale> availableLocales = new ArrayList<>(allFields.length);
        for (final Field field : allFields) {
            final int modifiers = field.getModifiers();
            if (field.getType() == Locale.class && Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                try {
                    availableLocales.add((Locale) field.get(Locale.class));
                } catch (final IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException("Field " + field, e);
                }
            }
        }
        availableLocales.sort(ObjectToStringComparator.INSTANCE);
        return availableLocales;
    }

    private final Locale locale;

    @Rule
    public final SetDefaultLocaleTestRule rule;

    public AbstractAvailableLocalesTest(final Locale locale)  {
        this.locale = locale;
        this.rule = new SetDefaultLocaleTestRule(locale);
    }

    public Locale getLocale() {
        return locale;
    }
}

