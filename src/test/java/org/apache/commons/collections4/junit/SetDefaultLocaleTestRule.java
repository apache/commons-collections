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

import java.util.Locale;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Sets the default {@code Locale} to the given locale for the duration of the test.
 */
public class SetDefaultLocaleTestRule implements TestRule {

    private final Locale locale;

    public SetDefaultLocaleTestRule(final Locale locale) {
        super();
        this.locale = locale;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final Locale savedLocale = Locale.getDefault();
                Locale.setDefault(getLocale());
                try {
                    base.evaluate();
                } finally {
                    Locale.setDefault(savedLocale);
                }
            }
        };
    }

    public Locale getLocale() {
        return locale;
    }

}
