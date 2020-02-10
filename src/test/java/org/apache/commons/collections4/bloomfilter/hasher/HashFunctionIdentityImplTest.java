/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertEquals;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.Signedness;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;
import org.junit.Test;

/**
 * Tests the HashFunctionIdentity implementation.
 *
 */
public class HashFunctionIdentityImplTest {

    /**
     * Tests a copy constructor of the HashFunctionIdentity.
     */
    @Test
    public void copyConstructorTest() {
        final HashFunctionIdentity identity = new HashFunctionIdentity() {

            @Override
            public String getName() {
                return "NAME";
            }

            @Override
            public String getProvider() {
                return "Provider";
            }

            @Override
            public Signedness getSignedness() {
                return Signedness.SIGNED;
            }

            @Override
            public ProcessType getProcessType() {
                return ProcessType.CYCLIC;
            }

            @Override
            public long getSignature() {
                return -1l;
            }

        };
        final HashFunctionIdentityImpl impl = new HashFunctionIdentityImpl( identity );
        assertEquals( "NAME", impl.getName());
        assertEquals( "Provider", impl.getProvider());
        assertEquals( Signedness.SIGNED, impl.getSignedness());
        assertEquals( ProcessType.CYCLIC, impl.getProcessType());
        assertEquals( -1l, impl.getSignature());
    }

    /**
     * Test the constructor from component values.
     * @param provider the name of the provider.
     * @param name the name of the hash function.
     * @param signedness the signedness of the hash function.
     * @param process the processes of the hash function.
     * @param signature the signature for the hash function.
     */
    @Test
    public void valuesConstructorTest() {
        final HashFunctionIdentityImpl impl = new HashFunctionIdentityImpl( "Provider", "NAME",
            Signedness.UNSIGNED, ProcessType.ITERATIVE, -2l);
        assertEquals( "NAME", impl.getName());
        assertEquals( "Provider", impl.getProvider());
        assertEquals( Signedness.UNSIGNED, impl.getSignedness());
        assertEquals( ProcessType.ITERATIVE, impl.getProcessType());
        assertEquals( -2l, impl.getSignature());
    }

}
