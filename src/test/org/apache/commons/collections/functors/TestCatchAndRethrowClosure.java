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

import java.io.IOException;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.FunctorException;
import org.junit.Assert;
import org.junit.Test;

public class TestCatchAndRethrowClosure extends BasicClosureTestBase {

    private static <T> Closure<T> generateIOExceptionClosure() {
        return new CatchAndRethrowClosure<T>() {

            @Override
            protected void executeAndThrow(T input) throws IOException  {
                throw new IOException();
            }
        };
    }

    private static <T> Closure<T> generateNullPointerExceptionClosure() {
        return new CatchAndRethrowClosure<T>() {

            @Override
            protected void executeAndThrow(T input) {
                throw new NullPointerException();
            }
        };
    }

    private static <T> Closure<T> generateNoExceptionClosure() {
        return new CatchAndRethrowClosure<T>() {

            @Override
            protected void executeAndThrow(T input) {
            }
        };
    }

    @Override
    protected <T> Closure<T> generateClosure() {
        return generateNoExceptionClosure();
    }
    
    @Test
    public void testThrowingClosure() {
        Closure<Integer> closure = generateNoExceptionClosure();
        try {
            closure.execute(Integer.valueOf(0));
        } catch (FunctorException ex) {
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.fail();
        }
        
        closure = generateIOExceptionClosure();
        try {
            closure.execute(Integer.valueOf(0));
            Assert.fail();
        } catch (FunctorException ex) {
            Assert.assertTrue(ex.getCause() instanceof IOException);
        } catch (RuntimeException ex) {
            Assert.fail();
        }

        closure = generateNullPointerExceptionClosure();
        try {
            closure.execute(Integer.valueOf(0));
            Assert.fail();
        } catch (FunctorException ex) {
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex instanceof NullPointerException);
        }
    }
}
