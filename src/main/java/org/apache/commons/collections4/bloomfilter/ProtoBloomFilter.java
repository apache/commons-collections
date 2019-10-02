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
package org.apache.commons.collections4.bloomfilter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * An object that contains the hashes necessary to construct any of a number of
 * BloomFilter implementations.
 *
 * <p> The ProtoBloomFilter contains the information
 * necessary to create a concrete Bloom filter with a given filter configuration. The
 * construction of the ProtoBloomFilter is far more compute expensive than making the
 * concrete bloom filter from the ProtoBloomFilter. Concrete implementations of BloomFilter
 * are built from the ProtoBloomFilter. </p>
 *
 * @see BloomFilterConfiguration
 * @since 4.5
 */
public final class ProtoBloomFilter implements Comparable<ProtoBloomFilter> {

    private final List<Hash> hashes;
    private final int hashCode;

    /**
     * An empty ProtoBloomFilter. Used to create empty BloomFilters.
     */
    public static final ProtoBloomFilter EMPTY = new ProtoBloomFilter(Collections.emptyList());

    /**
     * Gets a builder .
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /* package private for testing */
    /**
     * Constructor
     *
     * @param hashes the two longs that were created by the murmur hash function.
     */
    ProtoBloomFilter(Collection<Hash> hashes) {
        this.hashes = new ArrayList<Hash>();
        this.hashes.addAll(hashes);
        // sort so compareTo and equals work properly
        Collections.sort(this.hashes);
        hashCode = Objects.hash(hashes);
    }

    /**
     * Gets the count of hashed items included in this ProtoBloomFilter.
     *
     * @return The number of unique items in this ProtoBloomFilter.
     */
    public int getItemCount() {
        return hashes.size();
    }

    /**
     * Gets the stream of hashes included in this ProtoBloomFilter.
     *
     * @return the stream of hashes.
     */
    public Stream<Hash> getHashes() {
        return hashes.stream();
    }

    /**
     * Gets the count of unique hashed items included in this ProtoBloomFilter.
     *
     * @return the number of unique items hashed into the ProtoBloomFilter.
     */
    public int getUniqueItemCount() {
        return (int) getUniqueHashes().count();
    }

    /**
     * Gets the stream of unique hashes included in this ProtoBloomFilter.
     *
     * @return the stream of unique hashes.
     */
    public Stream<Hash> getUniqueHashes() {
        return hashes.stream().distinct();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(ProtoBloomFilter other) {
        Iterator<Hash> otherIter = other.getHashes().iterator();
        Iterator<Hash> iter = hashes.iterator();
        int result;
        while (iter.hasNext() && otherIter.hasNext()) {
            result = iter.next().compareTo(otherIter.next());
            if (result != 0) {
                return result;
            }
        }
        return otherIter.hasNext() ? -1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ProtoBloomFilter) {
            return compareTo((ProtoBloomFilter) o) == 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ProtoBloomFilter[ %s, %s]", hashes.size(), hashCode());
    }

    /**
     * A Bloom Filter hash calculation. This class only stores the result of an external
     * hash calculation. It does not perform the calculation itself. <p> The hash is
     * calculated as a 128-bit value. We store this as two 64-bit values. We can then
     * rapidly calculate the Bloom filter for any given configuration. </p>
     *
     * @since 4.5
     */
    public final static class Hash implements Comparable<Hash> {

        /**
         * The first 64 bits of the hash
         */
        private final long h1;
        /**
         * The second 64 bits of the hash
         */
        private final long h2;

        /**
         * The hash code for this Hash itself.
         */
        private int hashCode;

        /**
         * Constructor.
         *
         * @param h1 the first half of the 128-bit hash value
         * @param h2 the second half of the 128-bit hash value.
         */
        public Hash(long h1, long h2) {
            this.h1 = h1;
            this.h2 = h2;
            this.hashCode = Objects.hash(h1, h2);
        }

        /**
         * Turn on the appropriate bits in the bitset
         *
         * @param set the bit set to modify
         * @param config the filter configuration.
         * @return the set parameter for chaining.
         */
        public BitSet populate(BitSet set, BloomFilterConfiguration config) {
            for (int i = 0; i < config.getNumberOfHashFunctions(); i++) {
                int j = Math.abs((int) ((h1 + (i * h2)) % config.getNumberOfBits()));
                set.set(j, true);
            }
            return set;
        }

        /**
         * Gets a list of the indexes for bits that are turned on.
         *
         * The result of this method may contain duplicates.
         *
         * @param config the filter configuration to use
         * @return an array of ints enumerating the bits to be turned on.
         */
        public int[] getBits(BloomFilterConfiguration config) {
            int[] result = new int[config.getNumberOfHashFunctions()];

            for (int i = 0; i < config.getNumberOfHashFunctions(); i++) {
                result[i] = Math.abs((int) ((h1 + (i * h2)) % config.getNumberOfBits()));
            }
            return result;
        }

        @Override
        public int compareTo(Hash other) {
            int result = Long.compare(h1, other.h1);
            if (result == 0) {
                result = Long.compare(h2, other.h2);
            }
            return result;
        }

        @Override
        public int hashCode() {
            return hashCode;

        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Hash) {
                Hash other = (Hash) o;
                return h1 == other.h1 && h2 == other.h2;
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("Hash[ %s %s ]", Long.toHexString(h1), Long.toHexString(h2));
        }

    }

    /**
     * A ProtoBloomFilter Builder. <p> A Bloom filter may contain one or more items hashed
     * together to make the filter. </p> <p> There are two ways to hash the properties of
     * objects.</p> <ol> <li>One is to create a buffer containing all the properties and
     * hash that. This means that the search for the object must construct the same filter
     * and search for it. It is not possible to locate an object by a partial property
     * match. In this case each object is counted as a single item as specified in the
     * FilterConfig.</li> <li>The other is to hash each item separately. In this case each
     * of the properties can be searched for individually. However, in this case the
     * number of items that should be specified in the FilterConfig is the sum of the
     * cardinality of the properties being hashed.</li> </ol>
     *
     * @since 4.5
     */
    public static final class Builder {

        /**
         * The set of hashes used to build the ProtoBloomFilter.
         */
        private final Set<Hash> hashes;

        /**
         * Constructor.
         *
         */
        private Builder() {
            hashes = new HashSet<Hash>();
        }

        /**
         * Gets the collection of hashes from the builder.
         *
         * @return the collection of hashes.
         */
        public Set<Hash> getHashes() {
            return Collections.unmodifiableSet(hashes);
        }

        /**
         * Add the proto Bloom filter to this proto Bloom filter. The items included in
         * the parameter Bloom filter are added to the filter being built without
         * modification.
         *
         * @param protoBloomFilter The proto Bloom filter to add.
         * @return this for chaining
         */
        public Builder with(ProtoBloomFilter protoBloomFilter) {
            hashes.addAll(protoBloomFilter.hashes);
            return this;
        }

        /**
         * Add the byte buffer to the proto Bloom filter as a new hashed value.
         *
         * @param buffer The buffer to hash.
         * @return The ProtoBloomFilterBuilder for chaining.
         */
        public Builder with(ByteBuffer buffer) {
            hashes.add(hash3_x64_128(buffer, 0, buffer.limit(), 0L));
            return this;
        }

        /**
         * Add the byte to the proto Bloom filter filter as a new hashed value.
         *
         * @param b The byte to add.
         * @return this for chaining
         */
        public Builder with(byte b) {
            return with(ByteBuffer.wrap(new byte[] {b}));
        }

        /**
         * Add the bytes from the string to the proto Bloom filter as a new hashed value.
         *
         * The bytes are interpreted as UTF-8 chars.
         *
         * @param string The string to add.
         * @return this for chaining
         */
        public Builder with(String string) {
            return with(string.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Add a byte array to the proto Bloom filter filter as a new hashed value.
         *
         * @param buffer The buffer to add.
         * @return this for chaining
         */
        public Builder with(byte[] buffer) {
            return with(ByteBuffer.wrap(buffer));
        }

        /**
         * Reset the builder to its initial state.
         *
         * @return this for chaining
         */
        public Builder reset() {
            hashes.clear();
            return this;
        }

        /**
         * Build the ProtoBloomFilter.
         *
         * @return the defined ProtoBloomFilter.
         */
        public ProtoBloomFilter build() {
            return new ProtoBloomFilter(hashes);
        }

        /**
         * Add the ProtoBloomFilter to the filter as a new hash and build it.
         *
         * This is a convenience method for with(protoBloomFilter).build()
         *
         * @param protoBloomFilter The ProtoBloomFilter to add.
         * @return the defined ProtoBloomFilter.
         */
        public ProtoBloomFilter build(ProtoBloomFilter protoBloomFilter) {
            hashes.addAll(protoBloomFilter.hashes);
            return build();
        }

        /**
         * Add the byte buffer to the ProtoBloomFilter as a new hash and build it.
         *
         * This is a convenience method for with(buffer).build()
         *
         * @param buffer The buffer to add.
         * @return the defined ProtoBloomFilter.
         */
        public ProtoBloomFilter build(ByteBuffer buffer) {
            return with(buffer).build();
        }

        /**
         * Add the byte to the ProtoBloomFilter as a new hash and build it.
         *
         * This is a convenience method for with(b).build()
         *
         * @param b The byte to add.
         * @return the defined ProtoBloomFilter.
         */
        public ProtoBloomFilter build(byte b) {
            return with(b).build();
        }

        /**
         * Add the bytes from the string to the ProtoBloomFilter as a new hash and build
         * it.
         *
         * The bytes are interpreted as UTF-8 chars.
         *
         * This is a convenience method for with(string).build()
         *
         * @param string The string to add.
         * @return the defined ProtoBloomFilter.
         */
        public ProtoBloomFilter build(String string) {
            return with(string).build();
        }

        /**
         * Add the byte array to the ProtoBloomFilter as a new hash and build it.
         *
         * This is a convenience method for with(buffer).build()
         *
         * @param buffer The byte buffer to add.
         * @return the defined ProtoBloomFilter.
         */
        public ProtoBloomFilter build(byte[] buffer) {
            return with(buffer).build();
        }

        /**************************************
         * Methods to perform murmur 128 hash.
         **************************************/
        private long getblock(ByteBuffer key, int offset, int index) {
            int i_8 = index << 3;
            int blockOffset = offset + i_8;
            return ((long) key.get(blockOffset + 0) & 0xff) + (((long) key.get(blockOffset + 1) & 0xff) << 8) +
                (((long) key.get(blockOffset + 2) & 0xff) << 16) + (((long) key.get(blockOffset + 3) & 0xff) << 24) +
                (((long) key.get(blockOffset + 4) & 0xff) << 32) + (((long) key.get(blockOffset + 5) & 0xff) << 40) +
                (((long) key.get(blockOffset + 6) & 0xff) << 48) + (((long) key.get(blockOffset + 7) & 0xff) << 56);
        }

        private long rotl64(long v, int n) {
            return ((v << n) | (v >>> (64 - n)));
        }

        private long fmix(long k) {
            k ^= k >>> 33;
            k *= 0xff51afd7ed558ccdL;
            k ^= k >>> 33;
            k *= 0xc4ceb9fe1a85ec53L;
            k ^= k >>> 33;
            return k;
        }

        private Hash hash3_x64_128(ByteBuffer key, int offset, int length, long seed) {
            final int nblocks = length >> 4; // Process as 128-bit blocks.
            long h1 = seed;
            long h2 = seed;
            long c1 = 0x87c37b91114253d5L;
            long c2 = 0x4cf5ad432745937fL;
            // ----------
            // body
            for (int i = 0; i < nblocks; i++) {
                long k1 = getblock(key, offset, i * 2 + 0);
                long k2 = getblock(key, offset, i * 2 + 1);
                k1 *= c1;
                k1 = rotl64(k1, 31);
                k1 *= c2;
                h1 ^= k1;
                h1 = rotl64(h1, 27);
                h1 += h2;
                h1 = h1 * 5 + 0x52dce729;
                k2 *= c2;
                k2 = rotl64(k2, 33);
                k2 *= c1;
                h2 ^= k2;
                h2 = rotl64(h2, 31);
                h2 += h1;
                h2 = h2 * 5 + 0x38495ab5;
            }
            // ----------
            // tail
            // Advance offset to the unprocessed tail of the data.
            offset += nblocks * 16;
            long k1 = 0;
            long k2 = 0;
            switch (length & 15) {
            case 15:
                k2 ^= ((long) key.get(offset + 14)) << 48;
                // fallthrough
            case 14:
                k2 ^= ((long) key.get(offset + 13)) << 40;
                // fallthrough
            case 13:
                k2 ^= ((long) key.get(offset + 12)) << 32;
                // fallthrough
            case 12:
                k2 ^= ((long) key.get(offset + 11)) << 24;
                // fallthrough
            case 11:
                k2 ^= ((long) key.get(offset + 10)) << 16;
                // fallthrough
            case 10:
                k2 ^= ((long) key.get(offset + 9)) << 8;
                // fallthrough
            case 9:
                k2 ^= ((long) key.get(offset + 8)) << 0;
                k2 *= c2;
                k2 = rotl64(k2, 33);
                k2 *= c1;
                h2 ^= k2;
                // fallthrough
            case 8:
                k1 ^= ((long) key.get(offset + 7)) << 56;
                // fallthrough
            case 7:
                k1 ^= ((long) key.get(offset + 6)) << 48;
                // fallthrough
            case 6:
                k1 ^= ((long) key.get(offset + 5)) << 40;
                // fallthrough
            case 5:
                k1 ^= ((long) key.get(offset + 4)) << 32;
                // fallthrough
            case 4:
                k1 ^= ((long) key.get(offset + 3)) << 24;
                // fallthrough
            case 3:
                k1 ^= ((long) key.get(offset + 2)) << 16;
                // fallthrough
            case 2:
                k1 ^= ((long) key.get(offset + 1)) << 8;
                // fallthrough
            case 1:
                k1 ^= (key.get(offset));
                k1 *= c1;
                k1 = rotl64(k1, 31);
                k1 *= c2;
                h1 ^= k1;
                break;
            default: // 0
                // do nothing

            }
            // ----------
            // finalization
            h1 ^= length;
            h2 ^= length;
            h1 += h2;
            h2 += h1;
            h1 = fmix(h1);
            h2 = fmix(h2);
            h1 += h2;
            h2 += h1;
            return new Hash(h1, h2);
        }

    }
}
