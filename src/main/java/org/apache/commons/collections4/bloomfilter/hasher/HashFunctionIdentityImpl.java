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

/**
 * An instance of HashFunctionIdentity that is suitable for deserializing
 * HashFunctionIdentity data from a stream or any other situation where the
 * hash function is not available but the identify of the function is required.
 *
 * @since 4.5
 */
public final class HashFunctionIdentityImpl implements HashFunctionIdentity {
    private final String name;
    private final String provider;
    private final Signedness signedness;
    private final ProcessType process;
    private final long signature;

    /**
     * Creates a copy of the HashFunctionIdentity.
     * @param identity the identity to copy.
     */
    public HashFunctionIdentityImpl( HashFunctionIdentity identity) {
        this.name = identity.getName();
        this.provider = identity.getProvider();
        this.signedness = identity.getSignedness();
        this.process = identity.getProcessType();
        this.signature = identity.getSignature();
    }

    /**
     * Creates a HashFunctionIdentity from component values.
     * @param provider the name of the provider.
     * @param name the name of the hash function.
     * @param signedness the signedness of the hash function.
     * @param process the processes of the hash function.
     * @param signature the signature for the hash function.
     */
    public HashFunctionIdentityImpl( String provider, String name, Signedness signedness, ProcessType process,
        long signature) {
        this.name = name;
        this.provider = provider;
        this.signedness = signedness;
        this.process = process;
        this.signature = signature;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public Signedness getSignedness() {
        return signedness;
    }

    @Override
    public ProcessType getProcessType() {
        return process;
    }

    @Override
    public long getSignature() {
        return signature;
    }

}
