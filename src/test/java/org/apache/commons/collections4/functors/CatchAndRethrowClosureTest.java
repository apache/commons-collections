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
package org.apache.commons.collections4.functors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.FunctorException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class CatchAndRethrowClosureTest extends AbstractClosureTest {

    private static <T> Closure<T> generateIOExceptionClosure() {
        return new CatchAndRethrowClosure<T>() {

            @Override
            protected void executeAndThrow(final T input) throws IOException  {
                throw new IOException();
            }
        };
    }

    private static <T> Closure<T> generateNullPointerExceptionClosure() {
        return new CatchAndRethrowClosure<T>() {

            @Override
            protected void executeAndThrow(final T input) {
                throw new NullPointerException();
            }
        };
    }

    private static <T> Closure<T> generateNoExceptionClosure() {
        return new CatchAndRethrowClosure<T>() {

            @Override
            protected void executeAndThrow(final T input) {
            }
        };
    }

    @Override
    protected <T> Closure<T> generateClosure() {
        return generateNoExceptionClosure();
    }

    @TestFactory
    public Collection<DynamicTest> testThrowingClosure() {

        return Arrays.asList(

                dynamicTest("Closure NoException", () -> {
                    final Closure<Integer> closure = generateNoExceptionClosure();
                    closure.execute(Integer.valueOf(0));
                }),

                dynamicTest("Closure IOException", () -> {
                    final Closure<Integer> closure = generateIOExceptionClosure();
                    final FunctorException thrown = assertThrows(FunctorException.class, () -> closure.execute(Integer.valueOf(0)));
                    assertTrue(thrown.getCause() instanceof IOException);
                }),

                dynamicTest("Closure NullPointerException", () -> {
                    final Closure<Integer> closure = generateNullPointerExceptionClosure();
                    assertThrows(NullPointerException.class, () -> closure.execute(Integer.valueOf(0)));
                })

        );
    }

}
